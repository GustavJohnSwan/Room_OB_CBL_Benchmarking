package com.bignerdranch.android.room_ob_cbl_benchmarking

import android.app.Application
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.DAO.OB_DAO
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.OB_ViewModel
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.database_setup.ObjectBoxProvider
import android.util.Log
class ObjectBox_BenchmarkRunner(application: Application) {



    private var store = ObjectBoxProvider.get()
    private var ob_DAO =
        OB_DAO(store)

    private val obViewModel = OB_ViewModel(application)

    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // HELPER FUNCTIONS

    // higher function that takes a function as an argument
    private fun <T> benchmark(
        name: String,
        operation: () -> T
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


    // used by pilot data test
    /*
    private fun <T> benchmark1(
        operation: () -> T
    ): T {
        val start = System.nanoTime()
        val result = operation()
        val elapsed = System.nanoTime() - start

        Log.d(
            "DB_BENCHMARK",
            "${elapsed / 1_000_000.0}"
        )

        return result
    }
     */


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________

    fun runBenchmarking_OB () {



        // pilot data experiment code
        /*
        obViewModel.createFreshDatabase_ObjectBox()
        store = ObjectBoxProvider.get()
        ob_DAO = OB_DAO(store)
        obViewModel.insertDataSet_ObjectBox(3)
        var date = obViewModel.findEntriesInSpecificDateBenchmarking_ObjectBox()
        for (i in 1.. 30) {
            val results = benchmark1() {
                ob_DAO.findEntriesInSpecificDate(date)
            }
        }

        return
         */

        // _____________________________________________________________________________________________
        // _____________________________________________________________________________________________
        // _____________________________________________________________________________________________
        // CRUD

        // INSERT BENCHMARKING - INSERT FULL ENTRIES
        // recreates database, inserts X+n amount of entries
        for (i in 1.. 5) {
            obViewModel.createFreshDatabase_ObjectBox()
            store = ObjectBoxProvider.get()
            ob_DAO = OB_DAO(store)
            var listOfEntityDataObjects = obViewModel.insertDataSetBenchmarking_ObjectBox(i)
            var results = benchmark("ObjectBox: insert entries | datasetVariant=$i") {
                ob_DAO.putEntries(listOfEntityDataObjects)
            }
        }



        // INSERT MAIN ENTRIES ONLY data set BENCHMARKING
        // recreates database, inserts X+n amount of entries
        for (i in 1.. 5) {
            obViewModel.createFreshDatabase_ObjectBox()
            store = ObjectBoxProvider.get()
            ob_DAO = OB_DAO(store)
            var listOfEntityDataObjects = obViewModel.insertMainDataSetBenchmarking_ObjectBox(i)
            var results = benchmark("ObjectBox: insert main entries | datasetVariant=$i") {
                ob_DAO.putMainEntries(listOfEntityDataObjects)
            }
        }


        // INSERT ONE ENTRY
        obViewModel.createFreshDatabase_ObjectBox()
        store = ObjectBoxProvider.get()
        ob_DAO = OB_DAO(store)
        var entry = obViewModel.insertMainEntryBenchmark_ObjectBox()
        var results0 = benchmark("ObjectBox: insert main entry") {
            ob_DAO.putMainEntry(entry)
        }



        // FIND ALL MAIN ENTRIES BENCHMARKING
        for (i in 1.. 5) {
            obViewModel.createFreshDatabase_ObjectBox()
            store = ObjectBoxProvider.get()
            ob_DAO = OB_DAO(store)
            obViewModel.insertMainDataSet_ObjectBox(i)
            var results = benchmark("ObjectBox: find all main entries | datasetVariant=$i") {
                ob_DAO.getAllMainEntries()
            }
        }

        // FIND MAIN ENTRY BENCHMARKING
        for (i in 1.. 5) {
            obViewModel.createFreshDatabase_ObjectBox()
            store = ObjectBoxProvider.get()
            ob_DAO = OB_DAO(store)
            obViewModel.insertMainDataSet_ObjectBox(i)
            var listOfIDs = obViewModel.getEntryByIdBenchmark_ObjectBox()
            var results = benchmark("ObjectBox: find main entry | datasetVariant=$i") {
                ob_DAO.getMainEntry(listOfIDs[0])
            }
        }


        // FIND MAIN ENTRIES BENCHMARKING
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
                    obViewModel.createFreshDatabase_ObjectBox()
                    store = ObjectBoxProvider.get()
                    ob_DAO = OB_DAO(store)
                    obViewModel.insertMainDataSet_ObjectBox(i)
                    var listOfDesiredIDs = obViewModel.getEntriesByIdsBenchmark_ObjectBox(j)
                    var results = benchmark("ObjectBox: find X amount of main entries | datasetVariant=$i") {
                        ob_DAO.getMainEntries(listOfDesiredIDs)
                    }
                }
            }
        }


        // UPDATE MAIN ENTRY BENCHMARKING
        for (i in 1.. 5) {
            obViewModel.createFreshDatabase_ObjectBox()
            store = ObjectBoxProvider.get()
            ob_DAO = OB_DAO(store)
            obViewModel.insertMainDataSet_ObjectBox(i)
            var entry = obViewModel.updateEntryBenchmark_ObjectBox()
            var results = benchmark("ObjectBox: update main entry | datasetVariant=$i") {
                ob_DAO.updateEntry(entry)
            }
        }


        // UPDATE MAIN ENTRIES BENCHMARKING
        for (i in 1.. 5) {
            for (j in 1.. i) {
                obViewModel.createFreshDatabase_ObjectBox()
                store = ObjectBoxProvider.get()
                ob_DAO = OB_DAO(store)
                obViewModel.insertMainDataSet_ObjectBox(i)
                var entries = obViewModel.updateEntriesBenchmark_ObjectBox(j)
                var results = benchmark("ObjectBox: update X main entries | datasetVariant=$i") {
                    ob_DAO.updateEntries(entries)
                }
            }
        }



        // DELETE ALL MAIN ENTRIES BENCHMARKING
        for (i in 1.. 5) {
            obViewModel.createFreshDatabase_ObjectBox()
            store = ObjectBoxProvider.get()
            ob_DAO = OB_DAO(store)
            obViewModel.insertMainDataSet_ObjectBox(i)
            var results = benchmark("ObjectBox: delete all main entries | datasetVariant=$i") {
                ob_DAO.deleteAllMainEntries()
            }
        }



        // DELETE MAIN ENTRY BENCHMARKING
        for (i in 1.. 5) {
            obViewModel.createFreshDatabase_ObjectBox()
            store = ObjectBoxProvider.get()
            ob_DAO = OB_DAO(store)
            obViewModel.insertMainDataSet_ObjectBox(i)
            var listOfIDs = obViewModel.deleteEntryByIdBenchmark_ObjectBox()
            var results = benchmark("ObjectBox: delete main entry | datasetVariant=$i") {
                ob_DAO.deleteMainEntry(listOfIDs[0])
            }
        }


        // DELETE MAIN ENTRIES BENCHMARKING
        for (i in 1.. 5) {
            for (j in 1.. i) {
                obViewModel.createFreshDatabase_ObjectBox()
                store = ObjectBoxProvider.get()
                ob_DAO = OB_DAO(store)
                obViewModel.insertMainDataSet_ObjectBox(i)
                var listOfDesiredIDs = obViewModel.deleteEntriesByIdsBenchmark_ObjectBox(j)
                var results = benchmark("ObjectBox: delete X main entries | datasetVariant=$i") {
                    ob_DAO.deleteMainEntries(listOfDesiredIDs)
                }
            }
        }



        // COUNT ALL MAIN ENTRIES
        for (i in 1.. 5) {
                obViewModel.createFreshDatabase_ObjectBox()
                store = ObjectBoxProvider.get()
                ob_DAO = OB_DAO(store)
                obViewModel.insertMainDataSet_ObjectBox(i)

                var results = benchmark("ObjectBox: COUNT all main entries | datasetVariant=$i") {
                    ob_DAO.countEntries()
                }
        }






        // _____________________________________________________________________________________________
        // _____________________________________________________________________________________________
        // _____________________________________________________________________________________________
        // MEDIUM QUERIES

        // Benchmark #1
        for (i in 1.. 5) {
            obViewModel.createFreshDatabase_ObjectBox()
            store = ObjectBoxProvider.get()
            ob_DAO = OB_DAO(store)
            obViewModel.insertDataSet_ObjectBox(i)
            var date = obViewModel.findEntriesInSpecificDateBenchmarking_ObjectBox()
            val results = benchmark("ObjectBox: specific date | datasetVariant=$i") {
                ob_DAO.findEntriesInSpecificDate(date)
            }
        }


        // Benchmark #2
        for (i in 1.. 5) {
            obViewModel.createFreshDatabase_ObjectBox()
            store = ObjectBoxProvider.get()
            ob_DAO = OB_DAO(store)
            obViewModel.insertDataSet_ObjectBox(i)
            var arguments2 = obViewModel.findEntriesInDateRangeBenchmarking_ObjectBox()
            val results2 = benchmark("ObjectBox: date range | datasetVariant=$i") {
                ob_DAO.findEntriesInDateRange(arguments2[0], arguments2[1])
            }
        }



        // Benchmark #3
        for (i in 1.. 5) {
            obViewModel.createFreshDatabase_ObjectBox()
            store = ObjectBoxProvider.get()
            ob_DAO = OB_DAO(store)
            obViewModel.insertDataSet_ObjectBox(i)
            var arguments3 = obViewModel.findNextEntriesBenchmarking_ObjectBox()
            var startDate = arguments3[0] as String
            var endDate = arguments3[1] as String
            var amount: Long = arguments3[2] as Long
            val results3 = benchmark("ObjectBox: next x entries | datasetVariant=$i") {
                ob_DAO.findNextEntries(startDate, endDate, amount)
            }
        }


        // Benchmark #4
        for (i in 1.. 5) {
            obViewModel.createFreshDatabase_ObjectBox()
            store = ObjectBoxProvider.get()
            ob_DAO = OB_DAO(store)
            obViewModel.insertDataSet_ObjectBox(i)
            var arguments5 = obViewModel.findEntriesWithSpecificReminderBenchmarking_ObjectBox()
            val results4 = benchmark("ObjectBox: specific reminder | datasetVariant=$i") {
                ob_DAO.findEntriesWithSpecificReminder(arguments5)
            }
        }


        // Benchmark #5
        for (i in 1.. 5) {
            obViewModel.createFreshDatabase_ObjectBox()
            store = ObjectBoxProvider.get()
            ob_DAO = OB_DAO(store)
            obViewModel.insertDataSet_ObjectBox(i)
            val results5 = benchmark("ObjectBox: any recurrence | datasetVariant=$i") {
                ob_DAO.findEntriesWithRecurrance()
            }
        }



        // _____________________________________________________________________________________________
        // _____________________________________________________________________________________________
        // _____________________________________________________________________________________________
        // ADVANCED QUERIES


        // Benchmark #6
        for (i in 1.. 5) {
            obViewModel.createFreshDatabase_ObjectBox()
            store = ObjectBoxProvider.get()
            ob_DAO = OB_DAO(store)
            obViewModel.insertDataSet_ObjectBox(i)
            var arguments6 = obViewModel.findEntriesInDateRangeReminderRepeat1OrRepeat2Benchmarking_ObjectBox()
            var startDate6 = arguments6[0] as String
            var endDate6 = arguments6[1] as String
            var specificReminder6 = arguments6[2] as String
            var specificRepeat16 = arguments6[3] as String
            var specificRepeat26 = arguments6[4] as String
            var limit6 = arguments6[5] as Long
            val results6 = benchmark("ObjectBox: date range & repeat1 OR repeat2 | datasetVariant=$i") {
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


        // Benchmark #7
        for (i in 1.. 5) {
            obViewModel.createFreshDatabase_ObjectBox()
            store = ObjectBoxProvider.get()
            ob_DAO = OB_DAO(store)
            obViewModel.insertDataSet_ObjectBox(i)
            var arguments7 = obViewModel.findEntriesReminderNullRepeatNotNullLimitOffsetBenchmarking_ObjectBox()
            val results7 = benchmark("ObjectBox: reminder null & repeat NOT null | datasetVariant=$i") {
                ob_DAO.findEntriesReminderNullRepeatNotNullLimitOffset(arguments7[0], arguments7[1])
            }
        }

        // Benchmark #8
        for (i in 1.. 5) {
            obViewModel.createFreshDatabase_ObjectBox()
            store = ObjectBoxProvider.get()
            ob_DAO = OB_DAO(store)
            obViewModel.insertDataSet_ObjectBox(i)
            val results8 = benchmark("ObjectBox: COUNT repats by type | datasetVariant=$i") {
                ob_DAO.countEntriesByRepeatType()
            }
        }

        // Benchmark #9
        for (i in 1.. 5) {
            obViewModel.createFreshDatabase_ObjectBox()
            store = ObjectBoxProvider.get()
            ob_DAO = OB_DAO(store)
            obViewModel.insertDataSet_ObjectBox(i)
            var arguments9 = obViewModel.findEarliestLatestEventTimesInRangeWithReminderBenchmarking_ObjectBox()
            val results9 = benchmark("ObjectBox: earliest and latest time in range & reminder | datasetVariant=$i") {
                ob_DAO.findEarliestLatestEventTimesInRangeWithReminder(
                    arguments9[0],
                    arguments9[1],
                    arguments9[2]
                )
            }
        }


        // Benchmark #10
        for (i in 1.. 5) {
            obViewModel.createFreshDatabase_ObjectBox()
            store = ObjectBoxProvider.get()
            ob_DAO = OB_DAO(store)
            obViewModel.insertDataSet_ObjectBox(i)
            var arguments10 = obViewModel.findEntriesContainsSpecificTextTimeIsLaterThanSpecifiedTimeBenchmarking_ObjectBox()
            var textFragment10 = arguments10[0] as String
            var timeFloor10 = arguments10[1] as Int
            val results10 = benchmark("ObjectBox: title has specific text | datasetVariant=$i") {
                ob_DAO.findEntriesContainsSpecificTextTimeIsLaterThanSpecifiedTime(textFragment10, timeFloor10)
            }
        }


    }
}