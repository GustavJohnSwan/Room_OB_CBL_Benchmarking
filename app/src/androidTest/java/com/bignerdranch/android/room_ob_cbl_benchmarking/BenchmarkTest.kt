package com.bignerdranch.android.room_ob_cbl_benchmarking

import android.app.Application
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.database_setup.ObjectBoxProvider
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

// logcat filer "DB_BENCHMARK"
// Run with ".\gradlew.bat :app:connectedDebugAndroidTest" in terminal for debug build
// Run with ".\gradlew.bat :app:connectedBenchmarkAndroidTest" in terminal for benhcmark build
@RunWith(AndroidJUnit4::class)
class BenchmarkTest {

    @Test
    fun runBenchmarks() = runBlocking {

        val context = InstrumentationRegistry
            .getInstrumentation()
            .targetContext

        val application = InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .applicationContext as Application

        ObjectBoxProvider.init(application)

        // the actual benchmark
        val runner = ObjectBox_BenchmarkRunner(application)
            runner.runBenchmarking_OB()

        val roomRunner = Room_BenchmarkRunner(application)
        roomRunner.runBenchmarking_Room()







    }
}