package com.qusai.tarneebscoretracker.model

import kotlinx.serialization.Serializable

@Serializable
enum class GameStatus {
    IN_PROGRESS,
    GAME_OVER,
    CONTINUING,
}

@Serializable
data class GameState(
    val hasCompletedSetup: Boolean = false,
    val teamOne: Team = Team(TeamSide.TEAM_ONE, "Team 1"),
    val teamTwo: Team = Team(TeamSide.TEAM_TWO, "Team 2"),
    val targetScore: Int = 31,
    val teamOneScore: Int = 0,
    val teamTwoScore: Int = 0,
    val rounds: List<Round> = emptyList(),
    val gameStatus: GameStatus = GameStatus.IN_PROGRESS,
    val continuedAfterGameOver: Boolean = false,
) {
    fun nameFor(side: TeamSide): String = when (side) {
        TeamSide.TEAM_ONE -> teamOne.name
        TeamSide.TEAM_TWO -> teamTwo.name
    }

    fun scoreFor(side: TeamSide): Int = when (side) {
        TeamSide.TEAM_ONE -> teamOneScore
        TeamSide.TEAM_TWO -> teamTwoScore
    }

    fun leader(): TeamSide? = when {
        teamOneScore > teamTwoScore -> TeamSide.TEAM_ONE
        teamTwoScore > teamOneScore -> TeamSide.TEAM_TWO
        else -> null
    }

    fun winnerAtTarget(): TeamSide? {
        val teamOneReached = teamOneScore >= targetScore
        val teamTwoReached = teamTwoScore >= targetScore
        return when {
            !teamOneReached && !teamTwoReached -> null
            teamOneReached && !teamTwoReached -> TeamSide.TEAM_ONE
            teamTwoReached && !teamOneReached -> TeamSide.TEAM_TWO
            teamOneScore > teamTwoScore -> TeamSide.TEAM_ONE
            teamTwoScore > teamOneScore -> TeamSide.TEAM_TWO
            else -> null
        }
    }
}
