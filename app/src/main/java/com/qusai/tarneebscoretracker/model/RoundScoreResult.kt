package com.qusai.tarneebscoretracker.model

data class RoundScoreResult(
    val biddingTeam: TeamSide,
    val biddingTeamScoreChange: Int,
    val opposingTeamScoreChange: Int,
    val biddingTeamSucceeded: Boolean,
    val opposingTeamTricks: Int,
    val scoringType: ScoringType,
)
