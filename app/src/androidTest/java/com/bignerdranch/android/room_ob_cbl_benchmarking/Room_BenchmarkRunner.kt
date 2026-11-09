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
            var results = benchmark("Room: insert entries | datasetVariant=$i") {
                room_DAO.insertEntries(listOfEntityDataObjects)
            }
        }


        // INSERT MAIN ENTRIES ONLY data set BENCHMARKING
        // recreates database, inserts X+n amount of entries
        for (i in 1.. 5) {
            roomViewModel.createFreshDatabase_Room()
            room_DAO = roomViewModel.room_DAO
            var listOfEntityDataObjects = roomViewModel.insertMainDataSetBenchmarking_Room(i)
            var results = benchmark("Room: insert main entries | datasetVariant=$i") {
                room_DAO.insertMainEntries(listOfEntityDataObjects)
            }
        }


        // INSERT ONE ENTRY
            roomViewModel.createFreshDatabase_Room()
            room_DAO = roomViewModel.room_DAO
            var entry = roomViewModel.insertEntryBenchmark_Room()
            var results = benchmark("Room: insert main entry") {
                room_DAO.insertMainEntry(entry)
            }



        // FIND ALL MAIN ENTRIES BENCHMARKING
        for (i in 1.. 5) {
            roomViewModel.createFreshDatabase_Room()
            room_DAO = roomViewModel.room_DAO
            roomViewModel.insertMainDataSet_Room(i)
            var results = benchmark("Room: find all main entries | datasetVariant=$i") {
                room_DAO.getAllEntriesBulk()
            }
        }


        // FIND MAIN ENTRY BENCHMARKING
        for (i in 1.. 5) {
            roomViewModel.createFreshDatabase_Room()
            room_DAO = roomViewModel.room_DAO
            roomViewModel.insertMainDataSet_Room(i)
            var listOfIDs = roomViewModel.getEntryByIdBenchmark_Room()
            var results = benchmark("Room: find main entry | datasetVariant=$i") {
                room_DAO.getSpecificEntry(listOfIDs[0])
            }
        }

        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // TODO: Need to change the dataset of IDs for this query benchmarking
        // TODO: so that it is below SQLite’s maximum number of bound parameters per SQL statement
        // TODO: ID sets to use : 100, 1000, 10 000, 20 000, 30 000, 32 766 (max)
        // do this after all BenhcmarkRunners are finnished

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
                if (databaseSize >= roomViewModel.firstXamountIDs2_Room(j)) {
                    roomViewModel.createFreshDatabase_Room()
                    room_DAO = roomViewModel.room_DAO
                    roomViewModel.insertMainDataSet_Room(i)
                    var listOfIDs = roomViewModel.getEntriesByIdsBenchmark_Room(j)
                    var results = benchmark("Room: find X amount of main entries | datasetVariant=$i") {
                            room_DAO.getEntriesByIDs(listOfIDs)
                        }
                }
            }
        }

        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


        // UPDATE MAIN ENTRY BENCHMARKING
        for (i in 1.. 5) {
            roomViewModel.createFreshDatabase_Room()
            room_DAO = roomViewModel.room_DAO
            roomViewModel.insertMainDataSet_Room(i)
            var entry = roomViewModel.updateEntryBenchmark_Room()
            var results = benchmark("Room: update main entry | datasetVariant=$i") {
                room_DAO.updateEntry(entry)
            }
        }


        // UPDATE MAIN ENTRIES BENCHMARKING
        for (i in 1.. 5) {
            for (j in 1.. i) {
                roomViewModel.createFreshDatabase_Room()
                room_DAO = roomViewModel.room_DAO
                roomViewModel.insertMainDataSet_Room(i)
                var amount = roomViewModel.firstXamountIDs_Room(j)
                var entries = roomViewModel.updateEntriesBenchmark_Room(amount)
                var results = benchmark("Room: update X main entries | datasetVariant=$i") {
                    room_DAO.updateEntries(entries)
                }
            }
        }



        // DELETE ALL MAIN ENTRIES BENCHMARKING
        for (i in 1.. 5) {
            roomViewModel.createFreshDatabase_Room()
            room_DAO = roomViewModel.room_DAO
            roomViewModel.insertMainDataSet_Room(i)
            var results = benchmark("Room: delete all main entries | datasetVariant=$i") {
                room_DAO.deleteAllEntries()
            }
        }


        // DELETE MAIN ENTRY BENCHMARKING
        for (i in 1.. 5) {
            roomViewModel.createFreshDatabase_Room()
            room_DAO = roomViewModel.room_DAO
            roomViewModel.insertMainDataSet_Room(i)
            val entryToDelete = roomViewModel.deleteEntryByIdBenchmark_Room()
            val result = benchmark("Room: delete main entry | datasetVariant=$i") {
                room_DAO.deleteEntry(entryToDelete)
            }
        }


        // DELETE MAIN ENTRIES BENCHMARKING
        for (i in 1.. 5) {
            for (j in 1.. i) {
                roomViewModel.createFreshDatabase_Room()
                room_DAO = roomViewModel.room_DAO
                roomViewModel.insertMainDataSet_Room(i)
                val entriesToDelete = roomViewModel.deleteEntriesByIdsBenchmark_Room(j)
                val result = benchmark(
                    "Room: delete main entries | datasetVariant=$i | amount=${entriesToDelete.size}"
                ) {
                    room_DAO.deleteEntries(entriesToDelete)
                }
            }
        }


        // COUNT ALL MAIN ENTRIES
        for (i in 1.. 5) {
            roomViewModel.createFreshDatabase_Room()
            room_DAO = roomViewModel.room_DAO
            roomViewModel.insertMainDataSet_Room(i)
            val result = benchmark("Room: COUNT all main entries | datasetVariant=$i") {
                room_DAO.countEntries()
            }
        }


        // _____________________________________________________________________________________________
        // _____________________________________________________________________________________________
        // _____________________________________________________________________________________________
        // MEDIUM QUERIES


        // Benchmark #1
        for (i in 1.. 5) {
            roomViewModel.createFreshDatabase_Room()
            room_DAO = roomViewModel.room_DAO
            roomViewModel.insertDataSet_Room(i)
            val date = roomViewModel.findEntriesInSpecificDateBenchmarking_Room()
            val result = benchmark("Room: specific date | datasetVariant=$i") {
                room_DAO.findEntriesInSpecificDate(date)
            }
        }

        // Benchmark #2
        for (i in 1.. 5) {
            roomViewModel.createFreshDatabase_Room()
            room_DAO = roomViewModel.room_DAO
            roomViewModel.insertDataSet_Room(i)
            val arguments2 = roomViewModel.findEntriesInDateRangeBenchmarking_Room()
            val result = benchmark("Room: date range | datasetVariant=$i") {
                room_DAO.findEntriesInDateRange(arguments2[0], arguments2[1])
            }
        }


        // Benchmark #3
        for (i in 1.. 5) {
            roomViewModel.createFreshDatabase_Room()
            room_DAO = roomViewModel.room_DAO
            roomViewModel.insertDataSet_Room(i)
            val arguments3 = roomViewModel.findNextEntriesBenchmarking_Room()
            var startDate = arguments3[0] as String
            var endDate = arguments3[1] as String
            val amount = arguments3[2] as Int
            val result = benchmark("Room: next x entries | datasetVariant=$i") {
                room_DAO.findNextEntries(startDate, endDate, amount)
            }
        }


        // Benchmark #4
        for (i in 1.. 5) {
            roomViewModel.createFreshDatabase_Room()
            room_DAO = roomViewModel.room_DAO
            roomViewModel.insertDataSet_Room(i)
            val arguments5 = roomViewModel.findEntriesWithSpecificReminderBenchmarking_Room()
            val result = benchmark("Room: specific reminder | datasetVariant=$i") {
                room_DAO.findEntriesWithSpecificReminder(arguments5)
            }
        }



        // Benchmark #5
        for (i in 1.. 5) {
            roomViewModel.createFreshDatabase_Room()
            room_DAO = roomViewModel.room_DAO
            roomViewModel.insertDataSet_Room(i)
            val result = benchmark("Room: any recurrence | datasetVariant=$i") {
                room_DAO.findEntriesWithRecurrence()
            }
        }


        // _____________________________________________________________________________________________
        // _____________________________________________________________________________________________
        // _____________________________________________________________________________________________
        // ADVANCED QUERIES

        // Benchmark #6
        for (i in 1.. 5) {
            roomViewModel.createFreshDatabase_Room()
            room_DAO = roomViewModel.room_DAO
            roomViewModel.insertDataSet_Room(i)
            val arguments6 = roomViewModel.findEntriesInDateRangeReminderRepeat1OrRepeat2Benchmarking_Room()
            var startDate6 = arguments6[0] as String
            var endDate6 = arguments6[1] as String
            var specificReminder6 = arguments6[2] as String
            var specificRepeat16 = arguments6[3] as String
            var specificRepeat26 = arguments6[4] as String
            val limit6 = arguments6[5] as Int
            val result = benchmark("Room: date range & repeat1 OR repeat2 | datasetVariant=$i") {
                room_DAO.findEntriesInDateRangeReminderRepeat1OrRepeat2(
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
            roomViewModel.createFreshDatabase_Room()
            room_DAO = roomViewModel.room_DAO
            roomViewModel.insertDataSet_Room(i)
            val arguments7 = roomViewModel.findEntriesReminderNullRepeatNotNullLimitOffsetBenchmarking_Room()
            val result = benchmark("Room: reminder null & repeat NOT null | datasetVariant=$i") {
                room_DAO.findEntriesReminderNullRepeatNotNullLimitOffset(arguments7[0], arguments7[1])
            }
        }


        // Benchmark #8
        for (i in 1.. 5) {
            roomViewModel.createFreshDatabase_Room()
            room_DAO = roomViewModel.room_DAO
            roomViewModel.insertDataSet_Room(i)
            val result = benchmark("Room: COUNT repats by type | datasetVariant=$i") {
                room_DAO.countEntriesByRepeatType()
            }
        }


        // Benchmark #9
        for (i in 1.. 5) {
            roomViewModel.createFreshDatabase_Room()
            room_DAO = roomViewModel.room_DAO
            roomViewModel.insertDataSet_Room(i)
            val arguments9 = roomViewModel.findEarliestLatestEventTimesInRangeWithReminderBenchmarking_Room()
            val result = benchmark("Room: earliest and latest time in range & reminder | datasetVariant=$i") {
                room_DAO.findEarliestEventsInRangeWithReminder(
                    arguments9[0],
                    arguments9[1],
                    arguments9[2]
                )
            }
        }


        // Benchmark #10
        for (i in 1.. 5) {
            roomViewModel.createFreshDatabase_Room()
            room_DAO = roomViewModel.room_DAO
            roomViewModel.insertDataSet_Room(i)
            val arguments10 = roomViewModel.findEntriesContainsSpecificTextTimeIsLaterThanSpecifiedTimeBenchmarking_Room()
            var textFragment10 = arguments10[0] as String
            var timeFloor10 = arguments10[1] as Int
            val result = benchmark("Room: title has specific text | datasetVariant=$i") {
                room_DAO.findEntriesContainsSpecificTextTimeIsLaterThanSpecifiedTime(textFragment10, timeFloor10)
            }
        }





    }










}