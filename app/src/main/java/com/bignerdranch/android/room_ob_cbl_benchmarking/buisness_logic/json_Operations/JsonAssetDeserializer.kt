package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations

import android.content.Context
import kotlinx.serialization.*
import kotlinx.serialization.json.*

class JsonAssetDeserializer() {

    fun deserializeJson(jsonString: String): GeneratedEvent {
        var obj = Json.decodeFromString<GeneratedEvent>(jsonString)

        return obj
    }



}