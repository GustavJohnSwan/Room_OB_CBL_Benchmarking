package com.bignerdranch.android.room_ob_cbl_benchmarking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.ObjectBoxProvider
import com.bignerdranch.android.room_ob_cbl_benchmarking.ui.theme.Room_OB_CBL_BenchmarkingTheme
import com.bignerdranch.android.room_ob_cbl_benchmarking.ui_composables.BenchmarkScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ObjectBoxProvider.init(applicationContext) // Initialize ObjectBox
        enableEdgeToEdge()
        setContent {
            Room_OB_CBL_BenchmarkingTheme {
                BenchmarkScreen()
            }

        }
    }
}