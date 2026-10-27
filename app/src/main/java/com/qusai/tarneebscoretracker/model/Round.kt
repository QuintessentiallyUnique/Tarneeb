package com.qusai.tarneebscoretracker.model

import kotlinx.serialization.Serializable

@Serializable
data class RoundInput(
    val id: Long,
    val biddingTeam: TeamSide,
    val bid: Int,
    val biddingTeamTricks: Int,
    val notes: String = "",
)

@Serializable
data class Round(
    val id: Long,
    val roundNumber: Int,
    val biddingTeam: TeamSide,
    val bid: Int,
    val biddingTeamTricks: Int,
    val opposingTeamTricks: Int,
    val notes: String,
    val biddingTeamSucceeded: Boolean,
    val scoringType: ScoringType,
    val teamOneScoreChange: Int,
    val teamTwoScoreChange: Int,
    val teamOneTotalAfter: Int,
    val teamTwoTotalAfter: Int,
) {
    fun toInput(): RoundInput = RoundInput(
        id = id,
        biddingTeam = biddingTeam,
        bid = bid,
        biddingTeamTricks = biddingTeamTricks,
        notes = notes,
    )
}
