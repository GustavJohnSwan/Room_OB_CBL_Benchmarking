package com.bignerdranch.android.room_ob_cbl_benchmarking

import android.app.Application
import android.util.Log
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.CBL_ViewModel
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.DAO.CBL_DAO

class CouchbaseLite_BenchmarkRunner(application: Application) {

    private var cbl_DAO = CBL_DAO()

    private val cblViewModel = CBL_ViewModel(application)


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

    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________


    fun runBenchmarking_CBL () {

        // _____________________________________________________________________________________________
        // _____________________________________________________________________________________________
        // _____________________________________________________________________________________________
        // CRUD


        // INSERT BENCHMARKING - INSERT FULL ENTRIES
        // recreates database, inserts X+n amount of entries
        for (i in 1.. 5) {
            cblViewModel.createFreshDatabase_CouchbaseLite()
            cbl_DAO = CBL_DAO()
            var listOfEntityDataObjects = cblViewModel.insertDataSetBenchmarking_CouchbaseLite(i)
            var results = benchmark("CouchbaseLite: insert entries | datasetVariant=$i") {
                cbl_DAO.insertDocuments(listOfEntityDataObjects)
            }
        }


        // INSERT MAIN ENTRIES ONLY data set BENCHMARKING
        // recreates database, inserts X+n amount of entries
        for (i in 1.. 5) {
            cblViewModel.createFreshDatabase_CouchbaseLite()
            cbl_DAO = CBL_DAO()
            var listOfEntityDataObjects = cblViewModel.insertMainDataSetBenchmark_CouchbaseLite(i)
            var results = benchmark("CouchbaseLite: insert main entries | datasetVariant=$i") {
                cbl_DAO.insertMainDocs(listOfEntityDataObjects)
            }
        }



        // INSERT ONE ENTRY
        cblViewModel.createFreshDatabase_CouchbaseLite()
        cbl_DAO = CBL_DAO()
        var entry = cblViewModel.insertMainEntryBenchmark_CouchbaseLite()
        var results0 = benchmark("CouchbaseLite: insert main entry") {
            cbl_DAO.insertMainDoc(entry)
        }



        // FIND ALL MAIN ENTRIES BENCHMARKING
        for (i in 1.. 5) {
            cblViewModel.createFreshDatabase_CouchbaseLite()
            cbl_DAO = CBL_DAO()
            cblViewModel.insertMainDataSet_CouchbaseLite(i)
            var results = benchmark("CouchbaseLite: find all main entries | datasetVariant=$i") {
                cbl_DAO.getAllDocuments()
            }
        }


        // FIND MAIN ENTRY BENCHMARKING
        for (i in 1.. 5) {
            cblViewModel.createFreshDatabase_CouchbaseLite()
            cbl_DAO = CBL_DAO()
            cblViewModel.insertMainDataSet_CouchbaseLite(i)
            var listOfIDs = cblViewModel.getEntryByIdBenchmark_CouchbaseLite()
            val documentId = listOfIDs[0].toString()
            val results = benchmark("CouchbaseLite: find main entry | datasetVariant=$i") {
                cbl_DAO.getDocumentById(documentId)
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
                if (databaseSize >= cblViewModel.firstXamountIDs2_CouchbaseLite(j)) {
                    cblViewModel.createFreshDatabase_CouchbaseLite()
                    cbl_DAO = CBL_DAO()
                    cblViewModel.insertMainDataSet_CouchbaseLite(i)
                    var listOfDesiredIDs = cblViewModel.getEntriesByIdsBenchmark_CouchbaseLite(j)
                    val result = benchmark(
                        "CouchbaseLite: find main entries | datasetVariant=$i | amount=${listOfDesiredIDs.size}"
                    ) {
                        cbl_DAO.getDocumentsById(listOfDesiredIDs)
                    }
                }
            }
        }



        // UPDATE MAIN ENTRY BENCHMARKING
        for (i in 1.. 5) {
            cblViewModel.createFreshDatabase_CouchbaseLite()
            cbl_DAO = CBL_DAO()
            cblViewModel.insertMainDataSet_CouchbaseLite(i)
            var entry = cblViewModel.updateEntryBenchmark_CouchbaseLite()
            var results = benchmark("CouchbaseLite: update main entry | datasetVariant=$i") {
                cbl_DAO.updateDocument(entry)
            }
        }



        // UPDATE MAIN ENTRIES BENCHMARKING
        for (i in 1.. 5) {
            for (j in 1.. i) {
                cblViewModel.createFreshDatabase_CouchbaseLite()
                cbl_DAO = CBL_DAO()
                cblViewModel.insertMainDataSet_CouchbaseLite(i)
                val amount = cblViewModel.firstXamountIDs_CouchbaseLite(j)
                val entries = cblViewModel.updateEntriesBenchmark_CouchbaseLite(amount)
                var results = benchmark("CouchbaseLite: update X main entries | datasetVariant=$i") {
                    cbl_DAO.updateDocuments(entries)
                }
            }
        }


        // DELETE ALL MAIN ENTRIES BENCHMARKING
        for (i in 1.. 5) {
            cblViewModel.createFreshDatabase_CouchbaseLite()
            cbl_DAO = CBL_DAO()
            cblViewModel.insertMainDataSet_CouchbaseLite(i)
            var results = benchmark("CouchbaseLite: delete all main entries | datasetVariant=$i") {
                cbl_DAO.deleteAllDocuments()
            }
        }


        // DELETE MAIN ENTRY BENCHMARKING
        for (i in 1.. 5) {
            cblViewModel.createFreshDatabase_CouchbaseLite()
            cbl_DAO = CBL_DAO()
            cblViewModel.insertMainDataSet_CouchbaseLite(i)
            val documentId = cblViewModel.deleteEntryByIdBenchmark_CouchbaseLite()
            val result = benchmark("CouchbaseLite: delete main entry | datasetVariant=$i") {
                cbl_DAO.deleteDocument(documentId)
            }
        }


        // DELETE MAIN ENTRIES BENCHMARKING
        for (i in 1.. 5) {
            for (j in 1.. i) {
                cblViewModel.createFreshDatabase_CouchbaseLite()
                cbl_DAO = CBL_DAO()
                cblViewModel.insertMainDataSet_CouchbaseLite(i)
                var listOfDesiredIDs = cblViewModel.deleteEntriesByIdsBenchmark_CouchbaseLite(j)
                var results = benchmark("CouchbaseLite: delete X main entries | datasetVariant=$i") {
                    cbl_DAO.deleteDocuments(listOfDesiredIDs)
                }
            }
        }




        // COUNT ALL MAIN ENTRIES
        for (i in 1.. 5) {
            cblViewModel.createFreshDatabase_CouchbaseLite()
            cbl_DAO = CBL_DAO()
            cblViewModel.insertMainDataSet_CouchbaseLite(i)
            var results = benchmark("CouchbaseLite: COUNT all main entries | datasetVariant=$i") {
                cbl_DAO.countDocuments()
            }
        }


        // _____________________________________________________________________________________________
        // _____________________________________________________________________________________________
        // _____________________________________________________________________________________________
        // MEDIUM QUERIES


        // Benchmark #1
        for (i in 1.. 5) {
            cblViewModel.createFreshDatabase_CouchbaseLite()
            cbl_DAO = CBL_DAO()
            cblViewModel.insertDataSet_CouchbaseLite(i)
            var date = cblViewModel.findEntriesInSpecificDateBenchmarking_CouchbaseLite()
            var results = benchmark("CouchbaseLite: specific date | datasetVariant=$i") {
                cbl_DAO.findDocumentsInSpecificDate(date)
            }
        }


        // Benchmark #2
        for (i in 1.. 5) {
            cblViewModel.createFreshDatabase_CouchbaseLite()
            cbl_DAO = CBL_DAO()
            cblViewModel.insertDataSet_CouchbaseLite(i)
            var arguments2 = cblViewModel.findEntriesInDateRangeBenchmarking_CouchbaseLite()
            var results2 = benchmark("CouchbaseLite: date range | datasetVariant=$i") {
                cbl_DAO.findDocumentsInDateRange(arguments2[0], arguments2[1])
            }
        }


        // Benchmark #3
        for (i in 1.. 5) {
            cblViewModel.createFreshDatabase_CouchbaseLite()
            cbl_DAO = CBL_DAO()
            cblViewModel.insertDataSet_CouchbaseLite(i)
            var arguments3 = cblViewModel.findNextEntriesBenchmarking_CouchbaseLite()
            var startDate = arguments3[0] as String
            var endDate = arguments3[1] as String
            val amount = arguments3[2] as Int
            var results3 = benchmark("CouchbaseLite: next x entries | datasetVariant=$i") {
                cbl_DAO.findNextDocument(startDate, endDate, amount)
            }
        }


        // Benchmark #4
        for (i in 1.. 5) {
            cblViewModel.createFreshDatabase_CouchbaseLite()
            cbl_DAO = CBL_DAO()
            cblViewModel.insertDataSet_CouchbaseLite(i)
            var arguments4 = cblViewModel.findEntriesWithSpecificReminderBenchmarking_CouchbaseLite()
            var results4 = benchmark("CouchbaseLite: specific reminder | datasetVariant=$i") {
                cbl_DAO.findDocumentsWithSpecificReminder(arguments4)
            }
        }



        // Benchmark #5
        for (i in 1.. 5) {
            cblViewModel.createFreshDatabase_CouchbaseLite()
            cbl_DAO = CBL_DAO()
            cblViewModel.insertDataSet_CouchbaseLite(i)
            var results5 = benchmark("CouchbaseLite: any recurrence | datasetVariant=$i") {
                cbl_DAO.findDocumentsWithReccurence()
            }
        }



        // _____________________________________________________________________________________________
        // _____________________________________________________________________________________________
        // _____________________________________________________________________________________________
        // ADVANCED QUERIES

        // Benchmark #6
        for (i in 1.. 5) {
            cblViewModel.createFreshDatabase_CouchbaseLite()
            cbl_DAO = CBL_DAO()
            cblViewModel.insertDataSet_CouchbaseLite(i)
            var arguments6 = cblViewModel.findEntriesInDateRangeReminderRepeat1OrRepeat2Benchmarking_CouchbaseLite()
            var startDate6 = arguments6[0] as String
            var endDate6 = arguments6[1] as String
            var specificReminder6 = arguments6[2] as String
            var specificRepeat16 = arguments6[3] as String
            var specificRepeat26 = arguments6[4] as String
            val limit6 = arguments6[5] as Int
            var results6 = benchmark("CouchbaseLite: date range & repeat1 OR repeat2 | datasetVariant=$i") {
                cbl_DAO.findDocInDateRangeReminderRepeatOrRepeat(
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
            cblViewModel.createFreshDatabase_CouchbaseLite()
            cbl_DAO = CBL_DAO()
            cblViewModel.insertDataSet_CouchbaseLite(i)
            var arguments7 = cblViewModel.findEntriesReminderNullRepeatNotNullLimitOffsetBenchmarking_CouchbaseLite()
            var results7 = benchmark("CouchbaseLite: reminder null & repeat NOT null | datasetVariant=$i") {
                cbl_DAO.findDocumentsReminderNullRepeatNotNullLimitOffset(arguments7[0], arguments7[1])
            }
        }


        // Benchmark #8
        for (i in 1.. 5) {
            cblViewModel.createFreshDatabase_CouchbaseLite()
            cbl_DAO = CBL_DAO()
            cblViewModel.insertDataSet_CouchbaseLite(i)
            var results8 = benchmark("CouchbaseLite: COUNT repats by type | datasetVariant=$i") {
                cbl_DAO.countDocumentsByRepeatType()
            }
        }


        // Benchmark #9
        for (i in 1.. 5) {
            cblViewModel.createFreshDatabase_CouchbaseLite()
            cbl_DAO = CBL_DAO()
            cblViewModel.insertDataSet_CouchbaseLite(i)
            var arguments9 = cblViewModel.findEarliestLatestEventTimesInRangeWithReminderBenchmarking_CouchbaseLite()
            var results9 = benchmark("CouchbaseLite: earliest and latest time in range & reminder | datasetVariant=$i") {
                cbl_DAO.findEarliestDocumentsInRangeWithReminder(
                    arguments9[0],
                    arguments9[1],
                    arguments9[2]
                )
            }
        }



        // Benchmark #10
        for (i in 1.. 5) {
            cblViewModel.createFreshDatabase_CouchbaseLite()
            cbl_DAO = CBL_DAO()
            cblViewModel.insertDataSet_CouchbaseLite(i)
            var arguments10 = cblViewModel.findEntriesContainsSpecificTextTimeIsLaterThanSpecifiedTimeBenchmarking_CouchbaseLite()
            var textFragment10 = arguments10[0] as String
            var timeFloor10 = arguments10[1] as Int
            var results10 = benchmark("CouchbaseLite: title has specific text | datasetVariant=$i") {
                cbl_DAO.findDocTitleLikeTextTimeLater(textFragment10, timeFloor10)
            }
        }







    }
}