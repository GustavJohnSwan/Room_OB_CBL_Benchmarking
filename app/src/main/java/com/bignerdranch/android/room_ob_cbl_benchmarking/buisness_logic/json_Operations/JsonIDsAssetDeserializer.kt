package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations

import kotlinx.serialization.json.Json

class JsonIDsAssetDeserializer() {
    fun deserializeIDsJson(jsonString: String): List<Long> {
        var ids: List<Long> = Json.decodeFromString(jsonString)
        return ids
    }
}