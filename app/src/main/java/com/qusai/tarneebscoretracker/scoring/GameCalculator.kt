package com.qusai.tarneebscoretracker.scoring

import com.qusai.tarneebscoretracker.model.GameState
import com.qusai.tarneebscoretracker.model.GameStatus
import com.qusai.tarneebscoretracker.model.Round
import com.qusai.tarneebscoretracker.model.RoundInput
import com.qusai.tarneebscoretracker.model.Team
import com.qusai.tarneebscoretracker.model.TeamSide

/** Replays rounds from zero so edits, deletion, and undo cannot leave stale totals. */
object GameCalculator {
    fun newGame(teamOneName: String, teamTwoName: String, targetScore: Int): GameState {
        require(targetScore > 0) { "Target score must be greater than zero." }
        require(teamOneName.isNotBlank() || teamTwoName.isNotBlank()) {
            "Team names cannot both be empty."
        }

        return GameState(
            hasCompletedSetup = true,
            teamOne = Team(TeamSide.TEAM_ONE, teamOneName.ifBlank { "Team 1" }.trim()),
            teamTwo = Team(TeamSide.TEAM_TWO, teamTwoName.ifBlank { "Team 2" }.trim()),
            targetScore = targetScore,
        )
    }

    fun appendRound(state: GameState, input: RoundInput): GameState =
        recalculate(state, state.rounds.map(Round::toInput) + input)

    fun replaceRound(state: GameState, input: RoundInput): GameState = recalculate(
        state,
        state.rounds.map { round -> if (round.id == input.id) input else round.toInput() },
    )

    fun deleteRound(state: GameState, roundId: Long): GameState = recalculate(
        state,
        state.rounds.filterNot { it.id == roundId }.map(Round::toInput),
    )

    fun undoLastRound(state: GameState): GameState = recalculate(
        state,
        state.rounds.dropLast(1).map(Round::toInput),
    )

    fun recalculate(state: GameState, inputs: List<RoundInput>): GameState {
        // Derived totals are rebuilt from zero so no result from an edited or deleted round survives.
        var teamOneTotal = 0
        var teamTwoTotal = 0
        var firstTargetRoundIndex: Int? = null

        val rounds = inputs.mapIndexed { index, input ->
            val result = TarneebScoring.calculateRoundScore(
                biddingTeam = input.biddingTeam,
                bid = input.bid,
                biddingTeamTricks = input.biddingTeamTricks,
            )

            val teamOneChange = if (input.biddingTeam == TeamSide.TEAM_ONE) {
                result.biddingTeamScoreChange
            } else {
                result.opposingTeamScoreChange
            }
            val teamTwoChange = if (input.biddingTeam == TeamSide.TEAM_TWO) {
                result.biddingTeamScoreChange
            } else {
                result.opposingTeamScoreChange
            }

            teamOneTotal += teamOneChange
            teamTwoTotal += teamTwoChange
            if (firstTargetRoundIndex == null &&
                (teamOneTotal >= state.targetScore || teamTwoTotal >= state.targetScore)
            ) {
                firstTargetRoundIndex = index
            }

            Round(
                id = input.id,
                roundNumber = index + 1,
                biddingTeam = input.biddingTeam,
                bid = input.bid,
                biddingTeamTricks = input.biddingTeamTricks,
                opposingTeamTricks = result.opposingTeamTricks,
                notes = input.notes.trim(),
                biddingTeamSucceeded = result.biddingTeamSucceeded,
                scoringType = result.scoringType,
                teamOneScoreChange = teamOneChange,
                teamTwoScoreChange = teamTwoChange,
                teamOneTotalAfter = teamOneTotal,
                teamTwoTotalAfter = teamTwoTotal,
            )
        }

        val targetReached = teamOneTotal >= state.targetScore || teamTwoTotal >= state.targetScore
        val roundsExistAfterTarget = firstTargetRoundIndex?.let { it < rounds.lastIndex } == true
        val isContinuing = targetReached &&
            (state.continuedAfterGameOver || roundsExistAfterTarget)

        return state.copy(
            teamOneScore = teamOneTotal,
            teamTwoScore = teamTwoTotal,
            rounds = rounds,
            continuedAfterGameOver = isContinuing,
            gameStatus = when {
                !targetReached -> GameStatus.IN_PROGRESS
                isContinuing -> GameStatus.CONTINUING
                else -> GameStatus.GAME_OVER
            },
        )
    }
}
