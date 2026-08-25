package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic

import android.util.Log

// this was made to test if the read actually happenned. You can delete this function and its call
fun json_data_tests(jsonString: String?) {
    Log.d("JsonReader", "Char count in Json string : ${jsonString?.count()}")
}