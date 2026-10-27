package com.qusai.tarneebscoretracker.scoring

import com.qusai.tarneebscoretracker.model.RoundScoreResult
import com.qusai.tarneebscoretracker.model.ScoringType
import com.qusai.tarneebscoretracker.model.TeamSide

/** The single source of truth for Jawaker-style Tarneeb round scoring. */
object TarneebScoring {
    fun calculateRoundScore(
        biddingTeam: TeamSide,
        bid: Int,
        biddingTeamTricks: Int,
    ): RoundScoreResult {
        require(bid in 7..13) { "Bid must be between 7 and 13." }
        require(biddingTeamTricks in 0..13) { "Tricks must be between 0 and 13." }

        val opposingTeamTricks = 13 - biddingTeamTricks

        return when {
            bid == 13 && biddingTeamTricks == 13 -> RoundScoreResult(
                biddingTeam = biddingTeam,
                biddingTeamScoreChange = 26,
                opposingTeamScoreChange = 0,
                biddingTeamSucceeded = true,
                opposingTeamTricks = 0,
                scoringType = ScoringType.BID_13_SUCCESS,
            )

            bid == 13 -> RoundScoreResult(
                biddingTeam = biddingTeam,
                biddingTeamScoreChange = -16,
                opposingTeamScoreChange = opposingTeamTricks,
                biddingTeamSucceeded = false,
                opposingTeamTricks = opposingTeamTricks,
                scoringType = ScoringType.BID_13_FAILURE,
            )

            biddingTeamTricks == 13 -> RoundScoreResult(
                biddingTeam = biddingTeam,
                biddingTeamScoreChange = 16,
                opposingTeamScoreChange = 0,
                biddingTeamSucceeded = true,
                opposingTeamTricks = 0,
                scoringType = ScoringType.ALL_TRICKS_WITHOUT_13_BID,
            )

            biddingTeamTricks >= bid -> RoundScoreResult(
                biddingTeam = biddingTeam,
                biddingTeamScoreChange = biddingTeamTricks,
                opposingTeamScoreChange = 0,
                biddingTeamSucceeded = true,
                opposingTeamTricks = opposingTeamTricks,
                scoringType = ScoringType.NORMAL_SUCCESS,
            )

            else -> RoundScoreResult(
                biddingTeam = biddingTeam,
                biddingTeamScoreChange = -bid,
                opposingTeamScoreChange = opposingTeamTricks,
                biddingTeamSucceeded = false,
                opposingTeamTricks = opposingTeamTricks,
                scoringType = ScoringType.NORMAL_FAILURE,
            )
        }
    }
}
