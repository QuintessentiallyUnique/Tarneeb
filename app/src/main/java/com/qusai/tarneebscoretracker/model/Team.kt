package com.qusai.tarneebscoretracker.model

import kotlinx.serialization.Serializable

@Serializable
enum class TeamSide {
    TEAM_ONE,
    TEAM_TWO,
}

@Serializable
data class Team(
    val side: TeamSide,
    val name: String,
)

fun TeamSide.other(): TeamSide = when (this) {
    TeamSide.TEAM_ONE -> TeamSide.TEAM_TWO
    TeamSide.TEAM_TWO -> TeamSide.TEAM_ONE
}
