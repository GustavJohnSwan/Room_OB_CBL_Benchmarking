package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations

import kotlinx.serialization.Serializable
@Serializable
data class GeneratedExtraData(
    val reminderType: String?,
    val repeatType: String?,
    val repeatDetails: String?
)

@Serializable
data class GeneratedEvent(
    val fixtureId: Int,
    var date: String,
    val title: String,
    val time: Int,
    val extraData: GeneratedExtraData?
)