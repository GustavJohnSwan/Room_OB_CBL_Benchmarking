package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations

import android.content.Context


class JsonAssetReader(private val context: Context) {

    fun loadJsonFromAssets(fileName: String): String {

        return context.assets
            .open(fileName)
            .bufferedReader()
            .use { it.readText() }

    }

}
