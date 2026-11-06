package com.bignerdranch.android.room_ob_cbl_benchmarking

import android.app.Application
import android.util.Log
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.Room_ViewModel




class Room_BenchmarkRunner(application: Application) {



    private val roomViewModel = Room_ViewModel(application)
    private var room_DAO = roomViewModel.room_DAO


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // HELPER FUNCTIONS


    // higher function that takes a function as an argument
    private suspend fun <T> benchmark(
        name: String,
        operation: suspend () -> T
    ): T {
        val start = System.nanoTime()
        val result = operation()
        val elapsed = System.nanoTime() - start

        Log.d(
            "DB_BENCHMARK",
            "$name | timeNs=$elapsed | timeMs=${elapsed / 1_000_000.0}"
        )

        return result
    }

    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________


    suspend fun runBenchmarking_Room () {

        // _____________________________________________________________________________________________
        // _____________________________________________________________________________________________
        // _____________________________________________________________________________________________
        // CRUD

        // INSERT BENCHMARKING - INSERT FULL ENTRIES
        // recreates database, inserts X+n amount of entries
        for (i in 1.. 5) {
            roomViewModel.createFreshDatabase_Room()
            room_DAO = roomViewModel.room_DAO
            var listOfEntityDataObjects = roomViewModel.insertDataSetBenchmarking_Room(i)
            var results = benchmark("ObjectBox: insert entries | datasetVariant=$i") {
                room_DAO.insertEntries(listOfEntityDataObjects)
            }
        }



    }










}