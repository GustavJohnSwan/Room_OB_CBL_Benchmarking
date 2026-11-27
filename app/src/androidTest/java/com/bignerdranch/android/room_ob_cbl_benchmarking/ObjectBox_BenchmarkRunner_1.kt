package com.bignerdranch.android.room_ob_cbl_benchmarking

import android.app.Application
import android.util.Log
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.Room_ViewModel
import java.io.File

import android.os.ParcelFileDescriptor
import androidx.test.platform.app.InstrumentationRegistry
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.database_setup.AppDatabase

import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.PreparedBenchmarkData_4

import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.OB_ViewModel
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.DAO.OB_DAO
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.database_setup.ObjectBoxProvider


class ObjectBox_BenchmarkRunner_1(private val application: Application) {


    private var store = ObjectBoxProvider.get()
    private val obViewModel = OB_ViewModel(application)
    private var ob_DAO = OB_DAO(store)

    private var iterPos = 0
    private var repPos = 0
    private var timingCollector: TimingCollector? = null

    // private lateinit var dataObject4: PreparedBenchmarkData_4



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

        // collects and stores the execution time
        val collector = timingCollector
        if (collector != null) {
            collector.store(iterPos, repPos, elapsed)
            iterPos++
        }

        /*
        Log.d(
            "DB_BENCHMARK",
            "$name | timeNs=$elapsed | timeMs=${elapsed / 1_000_000.0}"
        )

         */

        return result
    }





    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________


    // function that handles benchmark iterations and repetitions
    private suspend fun iteratorFunction (benchmarkType: Int) {


        lateinit var query: suspend () -> Unit
        val prepare: suspend () -> Unit

        when (benchmarkType) {

            // Medium Queries
            1 -> {
                prepare = {
                    var date = obViewModel.findEntriesInSpecificDateBenchmarking_ObjectBox()

                    query = {
                        ob_DAO.findEntriesInSpecificDate(date)
                    }
                }
            }
            2 -> {
                prepare = {
                    var arguments2 = obViewModel.findEntriesInDateRangeBenchmarking_ObjectBox()

                    query = {
                        ob_DAO.findEntriesInDateRange(arguments2[0], arguments2[1])
                    }
                }
            }
            3 -> {
                prepare = {
                    var arguments3 = obViewModel.findNextEntriesBenchmarking_ObjectBox()
                    var startDate = arguments3[0] as String
                    var endDate = arguments3[1] as String
                    var amount: Long = arguments3[2] as Long

                    query = {
                        ob_DAO.findNextEntries(startDate, endDate, amount)
                    }
                }
            }
            4 -> {
                prepare = {
                    var arguments5 = obViewModel.findEntriesWithSpecificReminderBenchmarking_ObjectBox()

                    query = {
                        ob_DAO.findEntriesWithSpecificReminder(arguments5)
                    }
                }
            }
            5 -> {
                prepare = {
                    query = {
                        ob_DAO.findEntriesWithRecurrance()
                    }
                }
            }
            // Advanced Queries
            6 -> {
                prepare = {
                    var arguments6 = obViewModel.findEntriesInDateRangeReminderRepeat1OrRepeat2Benchmarking_ObjectBox()
                    var startDate6 = arguments6[0] as String
                    var endDate6 = arguments6[1] as String
                    var specificReminder6 = arguments6[2] as String
                    var specificRepeat16 = arguments6[3] as String
                    var specificRepeat26 = arguments6[4] as String
                    var limit6 = arguments6[5] as Long

                    query = {
                        ob_DAO.findEntriesInDateRangeReminderRepeat1OrRepeat2(
                            startDate6,
                            endDate6,
                            specificReminder6,
                            specificRepeat16,
                            specificRepeat26,
                            limit6
                        )
                    }
                }
            }
            7 -> {
                prepare = {
                    var arguments7 = obViewModel.findEntriesReminderNullRepeatNotNullLimitOffsetBenchmarking_ObjectBox()

                    query = {
                        ob_DAO.findEntriesReminderNullRepeatNotNullLimitOffset(arguments7[0], arguments7[1])
                    }
                }
            }
            8 -> {
                prepare = {
                    query = {
                        ob_DAO.countEntriesByRepeatType()
                    }
                }
            }
            9 -> {
                prepare = {
                    var arguments9 = obViewModel.findEarliestLatestEventTimesInRangeWithReminderBenchmarking_ObjectBox()

                    query = {
                        ob_DAO.findEarliestLatestEventTimesInRangeWithReminder(
                            arguments9[0],
                            arguments9[1],
                            arguments9[2]
                        )
                    }
                }
            }
            10 -> {
                prepare = {
                    var arguments10 = obViewModel.findEntriesContainsSpecificTextTimeIsLaterThanSpecifiedTimeBenchmarking_ObjectBox()
                    var textFragment10 = arguments10[0] as String
                    var timeFloor10 = arguments10[1] as Int

                    query = {
                        ob_DAO.findEntriesContainsSpecificTextTimeIsLaterThanSpecifiedTime(textFragment10, timeFloor10)
                    }
                }
            }

            // FIND ALL MAIN ENTRIES
            11 -> {
                prepare = {
                    query = {
                        ob_DAO.getAllMainEntries()
                    }
                }
            }

                // FIND MAIN ENTRY
            12 -> {
                prepare = {
                    var listOfIDs = obViewModel.getEntryByIdBenchmark_ObjectBox()

                    query = {
                        ob_DAO.getMainEntry(listOfIDs[0])
                    }
                }
            }


            // COUNT ALL MAIN ENTRY
            14 -> {
                prepare = {
                    query = {
                        ob_DAO.countEntries()
                    }
                }
            }

            else -> error("Unknown benchmark type: $benchmarkType")
        }

        for (i in 1..5) {

            // var i = 3
            val l: Int? = when (i) {
                1 -> null // 100 entries
                2 -> null // 1,000 entries
                3 -> null // 10,000 entries
                4 -> null // 50,000 entries
                5 -> null // 100,000 entries
                else -> error("Unknown dataset variant: $i")
            }


            /*
        val l: Int? = when (i) {
            1 -> 10
            2 -> 10
            3 -> 30
            4 -> 10
            5 -> 20
            else -> error("Unknown dataset variant: $i")
        }
         */


            var iterations = 10
            var repetitions = 1

            val pilotTimings = TimingCollector(iterations, repetitions)
            timingCollector = pilotTimings
            repPos = 0

            for (x in 1..repetitions) {

                obViewModel.createFreshDatabase_ObjectBox()
                store = ObjectBoxProvider.get()
                ob_DAO = OB_DAO(store)

                if (benchmarkType in 1..10) {
                    obViewModel.insertDataSet_ObjectBox(i)
                } else {
                    obViewModel.insertMainDataSet_ObjectBox(i)
                }

                prepare()

                iterPos = 0




                for (y in 1..(iterations + (l ?: 0))) {

                    if (l != null) {
                        if (y > l) {
                            val result =
                                benchmark("ObjectBox: query=$benchmarkType | datasetVariant=$i") {
                                    query()
                                }
                        } else {
                            query()
                        }
                    } else {
                        val result =
                            benchmark("ObjectBox: query=$benchmarkType | datasetVariant=$i") {
                                query()
                            }
                    }
                }

                repPos++
            }

            timingCollector = null

            val directory = File(
                checkNotNull(application.getExternalFilesDir(null)),
                "benchmark_results"
            )
            check(directory.isDirectory || directory.mkdirs())


            val mode = if (l == null) "Pilot" else "Production"

            val file = File(
                directory,
                "ObjectBox_Query${benchmarkType}_${mode}_variant${i}_warmup${l ?: 0}_${System.currentTimeMillis()}.tsv"
            )

            pilotTimings.saveTsv(file)

            Log.d("BENCHMARK_FILE", "Saved: ${file.absolutePath}")
        }
    }

    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________

    // SPECIAL HELPER FUNCTION FOR - GET ENTRIES BY IDs
    private suspend fun iteratorFunction_2 (benchmarkType: Int) {

        // var i = 3

        for (i in 1.. 5) {

            var databaseSize = 100

            when (i) {
                1 -> databaseSize = 100
                2 -> databaseSize = 1000
                3 -> databaseSize = 10000
                4 -> databaseSize = 50000
                5 -> databaseSize = 100000
            }

            for (j in 1.. 6) {
                if (databaseSize >= obViewModel.firstXamountIDs2_ObjectBox(j)) {

                    val l: Int? = when (i) {
                        1 -> null // 100 entries
                        2 -> null // 1,000 entries
                        3 -> null // 10,000 entries
                        4 -> null // 50,000 entries
                        5 -> null // 100,000 entries
                        else -> error("Unknown dataset variant: $i")
                    }

                    var iterations = 10
                    var repetitions = 1

                    val pilotTimings = TimingCollector(iterations, repetitions)
                    timingCollector = pilotTimings
                    repPos = 0

                    for (x in 1..repetitions) {

                        obViewModel.createFreshDatabase_ObjectBox()
                        store = ObjectBoxProvider.get()
                        ob_DAO = OB_DAO(store)
                        obViewModel.insertMainDataSet_ObjectBox(i)
                        var listOfDesiredIDs = obViewModel.getEntriesByIdsBenchmark_ObjectBox(j)

                        iterPos = 0

                        for (y in 1..(iterations + (l ?: 0))) {

                            if (l != null) {
                                if (y > l) {
                                    var results = benchmark("ObjectBox: query=13 | datasetVariant=$i | idVariant=$j") {
                                        ob_DAO.getMainEntries(listOfDesiredIDs)
                                    }
                                } else {
                                    ob_DAO.getMainEntries(listOfDesiredIDs)
                                }
                            } else {
                                var results = benchmark("ObjectBox: query=13 | datasetVariant=$i | idVariant=$j") {
                                    ob_DAO.getMainEntries(listOfDesiredIDs)
                                }
                            }
                        }

                        repPos++
                    }

                    timingCollector = null

                    val directory = File(
                        checkNotNull(application.getExternalFilesDir(null)),
                        "benchmark_results"
                    )
                    check(directory.isDirectory || directory.mkdirs())

                    val mode = if (l == null) "Pilot" else "Production"
                    val amount = obViewModel.firstXamountIDs2_ObjectBox(j)

                    val file = File(
                        directory,
                        "ObjectBox_FindXAmountOfMainEntries_${mode}_variant${i}_amount${amount}_warmup${l ?: 0}_${System.currentTimeMillis()}.tsv"
                    )

                    pilotTimings.saveTsv(file)

                    Log.d("BENCHMARK_FILE", "Saved: ${file.absolutePath}")
                }
            }
        }
    }



    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________

    private suspend fun iteratorFunction_3 (benchmarkType: Int) {





        for (i in 1..5) {


            lateinit var query: suspend () -> Unit
            val prepare: suspend () -> Unit

            when (benchmarkType) {

                // INSERT FULL ENTRIES
                15 -> {
                    prepare = {
                        var listOfEntityDataObjects = obViewModel.insertDataSetBenchmarking_ObjectBox(i)
                        query = {
                            ob_DAO.putEntries(listOfEntityDataObjects)
                        }
                    }
                }

                // INSERT MAIN ENTRIES
                16 -> {
                    prepare = {
                        var listOfEntityDataObjects = obViewModel.insertMainDataSetBenchmarking_ObjectBox(i)
                        query = {
                            ob_DAO.putMainEntries(listOfEntityDataObjects)
                        }
                    }
                }

                // INSERT ENTRY
                17 -> {
                    prepare = {
                        var entry = obViewModel.insertMainEntryBenchmark_ObjectBox()
                        query = {
                            ob_DAO.putMainEntry(entry)
                        }
                    }
                }

                // UPDATE MAIN ENTRY
                18 -> {
                    prepare = {
                        var entry = obViewModel.updateEntryBenchmark_ObjectBox()
                        query = {
                            ob_DAO.updateEntry(entry)
                        }
                    }
                }

                // DELETE ALL MAIN ENTRIES
                20 -> {
                    prepare = {
                        query = {
                            ob_DAO.deleteAllMainEntries()
                        }
                    }
                }

                // DELETE MAIN ENTRY
                21 -> {
                    prepare = {
                        var listOfIDs = obViewModel.deleteEntryByIdBenchmark_ObjectBox()
                        query = {
                            ob_DAO.deleteMainEntry(listOfIDs[0])
                        }
                    }
                }
                else -> error("Unknown benchmark type: $benchmarkType")
            }

            // var i = 3
            val l: Int? = when (i) {
                1 -> null // 100 entries
                2 -> null // 1,000 entries
                3 -> null // 10,000 entries
                4 -> null // 50,000 entries
                5 -> null // 100,000 entries
                else -> error("Unknown dataset variant: $i")
            }


            /*
        val l: Int? = when (i) {
            1 -> 10
            2 -> 10
            3 -> 30
            4 -> 10
            5 -> 20
            else -> error("Unknown dataset variant: $i")
        }
         */


            var iterations = 10
            var repetitions = 1

            val pilotTimings = TimingCollector(iterations, repetitions)
            timingCollector = pilotTimings
            repPos = 0

            for (x in 1..repetitions) {



                iterPos = 0




                for (y in 1..(iterations + (l ?: 0))) {

                    obViewModel.createFreshDatabase_ObjectBox()
                    store = ObjectBoxProvider.get()
                    ob_DAO = OB_DAO(store)

                    if (benchmarkType != 15 && benchmarkType != 16) {
                        obViewModel.insertMainDataSet_ObjectBox(i)
                    }

                    prepare()

                    if (l != null) {
                        if (y > l) {
                            val result =
                                benchmark("ObjectBox: query=$benchmarkType | datasetVariant=$i") {
                                    query()
                                }
                        } else {
                            query()
                        }
                    } else {
                        val result =
                            benchmark("ObjectBox: query=$benchmarkType | datasetVariant=$i") {
                                query()
                            }
                    }
                }

                repPos++
            }

            timingCollector = null

            val directory = File(
                checkNotNull(application.getExternalFilesDir(null)),
                "benchmark_results"
            )
            check(directory.isDirectory || directory.mkdirs())


            val mode = if (l == null) "Pilot" else "Production"

            val file = File(
                directory,
                "ObjectBox_Query${benchmarkType}_${mode}_variant${i}_warmup${l ?: 0}_${System.currentTimeMillis()}.tsv"
            )

            pilotTimings.saveTsv(file)

            Log.d("BENCHMARK_FILE", "Saved: ${file.absolutePath}")
        }
    }



    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________

    // THIS FUNCTION HASNT BEEN SETUP FULLY. IT WAS SKIPPED FOR NOW UNTIL I DECIDE TO IMPLEMENT SNAPSHOTS TO BENCHMARK Q19 AND Q22
    /*
    private suspend fun prepareBenchmarkData_4() {

        //dataObject4 = PreparedBenchmarkData_4("DataSets_4")

        val databaseSizes = listOf(100, 1000, 10000, 50000, 100000)

        for (j in 1..5) {
            dataObject4.amount.add(
                roomViewModel.firstXamountIDs_Room(j)
            )
        }

        for (i in 1..5) {

            val databaseSize = databaseSizes[i - 1]

            // Add empty containers for this dataset's batches.
            dataObject4.entries.add(mutableListOf())
            dataObject4.entriesToDelete.add(mutableListOf())

            dataObject4.mainEntries.add(
                roomViewModel.insertMainDataSetBenchmarking_Room(i)
            )

            for (j in 1..5) {

                val amount = dataObject4.amount[j - 1]

                if (databaseSize >= amount) {

                    dataObject4.entries[i - 1].add(
                        roomViewModel.updateEntriesBenchmark_Room(
                            amount,
                            databaseSize
                        )
                    )

                    dataObject4.entriesToDelete[i - 1].add(
                        roomViewModel.deleteEntriesByIdsBenchmark_Room(
                            j,
                            databaseSize
                        )
                    )
                }
            }
        }
    }


    // THIS FUNCTION HASNT BEEN SETUP FULLY. IT WAS SKIPPED FOR NOW UNTIL I DECIDE TO IMPLEMENT SNAPSHOTS TO BENCHMARK Q19 AND Q22
    private suspend fun iteratorFunction_4 (benchmarkType: Int) {




        // var i = 3

        //for (i in 1.. 5) {
        for (i in 5..5) {

            var databaseSize = 100

            when (i) {
                1 -> databaseSize = 100
                2 -> databaseSize = 1000
                3 -> databaseSize = 10000
                4 -> databaseSize = 50000
                5 -> databaseSize = 100000
            }

            for (j in 1.. 5) {
                if (databaseSize >= roomViewModel.firstXamountIDs_Room(j)) {

                    lateinit var query: suspend () -> Unit
                    val prepare: suspend () -> Unit

                    when (benchmarkType) {

                        // UPDATE MAIN ENTRIES
                        19 -> {
                            prepare = {
                                // var amount = roomViewModel.firstXamountIDs_Room(j)
                                // var entries = roomViewModel.updateEntriesBenchmark_Room(amount)

                                val entries = dataObject4.entries[i - 1][j - 1]
                                query = {
                                    room_DAO.updateEntries(entries)
                                }
                            }
                        }

                        // DELETE MAIN ENTRIES
                        22 -> {
                            prepare = {
                                // val entriesToDelete = roomViewModel.deleteEntriesByIdsBenchmark_Room(j)

                                val entriesToDelete = dataObject4.entriesToDelete[i - 1][j - 1]
                                query = {
                                    room_DAO.deleteEntries(entriesToDelete)
                                }
                            }
                        }

                        else -> error("Unknown benchmark type: $benchmarkType")
                    }


                    val l: Int? = when (i) {
                        1 -> null // 100 entries
                        2 -> null // 1,000 entries
                        3 -> null // 10,000 entries
                        4 -> null // 50,000 entries
                        5 -> null // 100,000 entries
                        else -> error("Unknown dataset variant: $i")
                    }

                    var iterations = 10
                    var repetitions = 1

                    val pilotTimings = TimingCollector(iterations, repetitions)
                    timingCollector = pilotTimings
                    repPos = 0



                    for (x in 1..repetitions) {


                        iterPos = 0

                        for (y in 1..(iterations + (l ?: 0))) {

                            /*
                            roomViewModel.createFreshDatabase_Room()
                            room_DAO = roomViewModel.room_DAO
                            room_DAO.insertMainEntries(
                                dataObject4.mainEntries[i - 1]
                            )
                             */

                            val startReset = System.nanoTime()

                            roomViewModel.createFreshDatabase_Room()
                            room_DAO = roomViewModel.room_DAO

                            val resetNs = System.nanoTime() - startReset

                            val startPopulation = System.nanoTime()

                            room_DAO.insertMainEntries(
                                dataObject4.mainEntries[i - 1]
                            )

                            val populationNs = System.nanoTime() - startPopulation




                            prepare()

                            if (l != null) {
                                if (y > l) {
                                    benchmark("Room: query=$benchmarkType | datasetVariant=$i | idVariant=$j") {
                                        query()
                                    }
                                } else {
                                    query()
                                }
                            } else {
                                benchmark("Room: query=$benchmarkType | datasetVariant=$i | idVariant=$j") {
                                    query()
                                }
                            }
                            Log.d(
                                "BENCHMARK_SETUP",
                                "query=$benchmarkType" +
                                        " | datasetVariant=$i" +
                                        " | amount=${dataObject4.amount[j - 1]}" +
                                        " | repetition=$x" +
                                        " | iteration=$y" +
                                        " | reset_ms=${resetNs / 1_000_000.0}" +
                                        " | population_ms=${populationNs / 1_000_000.0}"
                            )
                        }

                        repPos++
                    }

                    timingCollector = null

                    val directory = File(
                        checkNotNull(application.getExternalFilesDir(null)),
                        "benchmark_results"
                    )
                    check(directory.isDirectory || directory.mkdirs())

                    val mode = if (l == null) "Pilot" else "Production"
                    val amount = roomViewModel.firstXamountIDs_Room(j)

                    val file = File(
                        directory,
                        "Room_Query${benchmarkType}_${mode}_variant${i}_amount${amount}_warmup${l ?: 0}_${System.currentTimeMillis()}.tsv"
                    )

                    pilotTimings.saveTsv(file)

                    Log.d("BENCHMARK_FILE", "Saved: ${file.absolutePath}")
                }
            }
        }
    }

     */



    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________


    suspend fun runBenchmarking_ObjectBox () {

        // CRUD
        iteratorFunction(11)
        iteratorFunction(12)
        iteratorFunction_2(13)
        iteratorFunction(14)

        // CRUD - WRITES
        // testing postponed - need to implement a overhead reduction solution using snapshots for Q19 and Q22
        /*
        iteratorFunction_3(15)
        iteratorFunction_3(16)
        iteratorFunction_3(17)
        iteratorFunction_3(18)
        iteratorFunction_4(19)
        iteratorFunction_3(20)
        iteratorFunction_3(21)
        iteratorFunction_4(22)

         */

        // MEDIUM QUERIES
        iteratorFunction(1)
        iteratorFunction(2)
        iteratorFunction(3)
        iteratorFunction(4)
        iteratorFunction(5)

        // ADVANCED QUERIES
        iteratorFunction(6)
        iteratorFunction(7)
        iteratorFunction(8)
        iteratorFunction(9)
        iteratorFunction(10)


    }

/*
    suspend fun runBenchmarking_ObjectBox_2 () {

        // pre-setup data set for Q19 and Q22 to reduce overhead
        // prepareBenchmarkData_4()


        // CRUD
        iteratorFunction(11)
        iteratorFunction(12)
        iteratorFunction_2(13)
        iteratorFunction(14)

        // CRUD - WRITES
        iteratorFunction_3(15)
        iteratorFunction_3(16)
        iteratorFunction_3(17)
        iteratorFunction_3(18)
        //iteratorFunction_4(19)
        iteratorFunction_3(20)
        iteratorFunction_3(21)
        //iteratorFunction_4(22)



    }

 */

    /*
    suspend fun runBenchmarking_ObjectBox_3() {

        prepareBenchmarkData_4()

        // UPDATE MAIN ENTRIES
        //iteratorFunction_4(19)

        // DELETE MAIN ENTRIES
        //iteratorFunction_4(22)
    }

     */
}