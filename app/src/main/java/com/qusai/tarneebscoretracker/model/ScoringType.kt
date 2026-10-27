package com.qusai.tarneebscoretracker.model

import kotlinx.serialization.Serializable

@Serializable
enum class ScoringType {
    NORMAL_SUCCESS,
    NORMAL_FAILURE,
    ALL_TRICKS_WITHOUT_13_BID,
    BID_13_SUCCESS,
    BID_13_FAILURE,
}
