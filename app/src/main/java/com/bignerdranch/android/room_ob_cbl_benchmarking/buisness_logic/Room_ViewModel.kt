package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.JsonAssetDeserializer
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.JsonAssetReader
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.JsonIDsAssetDeserializer
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryTable
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.database_setup.AppDatabase

import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.data_mapping.Room_Mapping
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class Room_ViewModel (application: Application) : AndroidViewModel(application) {

     var db = AppDatabase.getDatabase(application)
     var room_DAO = db.Room_DAO()

     var jsonIDsAssetDeserializer =
        JsonIDsAssetDeserializer()

     var jsonAssetReader =
        JsonAssetReader(application.applicationContext)

     var jsonAssetDeserializer =
        JsonAssetDeserializer()

     var room_Mapping =
        Room_Mapping()


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // HELPER FUNCTIONS

    fun accessListOfIdsBySize_Room(databaseSize: Int): List<Long> {
        require(databaseSize in listOf(100, 1000, 10000, 50000, 100000)) {
            "No ID dataset exists for database size $databaseSize"
        }

        val jsonString =
            jsonAssetReader.loadJsonFromAssets("IDs_A${databaseSize}.json")

        return jsonIDsAssetDeserializer.deserializeIDsJson(jsonString)
    }
    
    // OUTDATED
    /*
    fun accessListOfIdsByVariant_Room (variant: Int): List<Long> {

        // default value
        var jsonFileNamePart = "100"

        when (variant) {
            1 -> jsonFileNamePart = "100"
            2 -> jsonFileNamePart = "1000"
            3 -> jsonFileNamePart = "10000"
            4 -> jsonFileNamePart = "50000"
            5 -> jsonFileNamePart = "100000"
        }

        var jsonString = jsonAssetReader.loadJsonFromAssets("IDs_A${jsonFileNamePart}.json")

        var listOfIDs = jsonIDsAssetDeserializer.deserializeIDsJson(jsonString)

        return listOfIDs
    }
     */



    fun firstXamountIDs_Room (IdVariant: Int): Int {

        var IdAmount = 100

        when (IdVariant) {
            1 -> IdAmount = 100
            2 -> IdAmount = 1000
            3 -> IdAmount = 10000
            4 -> IdAmount = 50000
            5 -> IdAmount = 100000
        }

        return IdAmount

    }


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________

    fun createFreshDatabaseAndDataSet_Room (variant: Int) {

        viewModelScope.launch(Dispatchers.IO) {
            db = AppDatabase.reset(
                getApplication<Application>().applicationContext
            )
            room_DAO = db.Room_DAO()

            insertDataSet_Room(variant)
        }



    }

     suspend fun insertDataSet_Room (variant: Int) {

        // default value
        var jsonFileNamePart = "100_S1"

        when (variant) {
            1 -> jsonFileNamePart = "100_S1"
            2 -> jsonFileNamePart = "1000_S2"
            3 -> jsonFileNamePart = "10000_S3"
            4 -> jsonFileNamePart = "50000_S4"
            5 -> jsonFileNamePart = "100000_S5"
        }

        // modify this to change .json file based on variant by replacing numbers in string
        val jsonString = jsonAssetReader.loadJsonFromAssets("events_A${jsonFileNamePart}.json")

        val listOfDataObjects = jsonAssetDeserializer.deserializeJson(jsonString)

        val listOfEntityDataObjects = room_Mapping.map(listOfDataObjects)

        var ids = room_DAO.insertEntries(listOfEntityDataObjects)

    }


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________

    fun createFreshDatabaseAndMainDataSet_Room (variant: Int) {

        viewModelScope.launch(Dispatchers.IO) {
            db = AppDatabase.reset(
                getApplication<Application>().applicationContext
            )
            room_DAO = db.Room_DAO()

            insertMainDataSet_Room(variant)
        }



    }

     suspend fun insertMainDataSet_Room (variant: Int) {

        // default value
        var jsonFileNamePart = "100_S1"

        when (variant) {
            1 -> jsonFileNamePart = "100_S1"
            2 -> jsonFileNamePart = "1000_S2"
            3 -> jsonFileNamePart = "10000_S3"
            4 -> jsonFileNamePart = "50000_S4"
            5 -> jsonFileNamePart = "100000_S5"
        }

        // modify this to change .json file based on variant by replacing numbers in string
        val jsonString = jsonAssetReader.loadJsonFromAssets("events_A${jsonFileNamePart}.json")

        val listOfDataObjects = jsonAssetDeserializer.deserializeJson(jsonString)

        val listOfEntityDataObjects = room_Mapping.mapMainEntries(listOfDataObjects)

        var ids = room_DAO.insertMainEntries(listOfEntityDataObjects)

    }

    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________


    // INSERT ONE ENTRY
     suspend fun insertEntryBenchmark_Room () {

        val entry = EntryTable(
            dateDB = "2026-08-27",
            entryDB = "TyOk6EFsIWL6L3YOY15gwiS",
            timeMinutes = 1427
        )

        room_DAO.insertMainEntry(entry)
    }


    // INSERT ENTRIES BULK
     suspend fun insertEntriesBenchmark_Room (variant: Int) {

        // default value
        var jsonFileNamePart = "100_S1"

        when (variant) {
            1 -> jsonFileNamePart = "100_S1"
            2 -> jsonFileNamePart = "1000_S2"
            3 -> jsonFileNamePart = "10000_S3"
            4 -> jsonFileNamePart = "50000_S4"
            5 -> jsonFileNamePart = "100000_S5"
        }

        val jsonString = jsonAssetReader.loadJsonFromAssets("events_A${jsonFileNamePart}.json")

        val listOfDataObjects = jsonAssetDeserializer.deserializeJson(jsonString)

        val listOfEntityDataObjects = room_Mapping.mapMainEntries(listOfDataObjects)

        room_DAO.insertMainEntries(listOfEntityDataObjects)

    }


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________

    // UPDATE ENTRY
     suspend fun updateEntryBenchmark_Room () {

        var dbSize = countEntriesBenchmark_Room().toInt()

        val IdAmount = 1

        var listOfIDs = accessListOfIdsBySize_Room(dbSize)

        var listOfRelevantIDs = listOfIDs.take(IdAmount)

        val updateJsonString = jsonAssetReader.loadJsonFromAssets("eventsForUpdate_A100000_S6.json")

        val listOfDataObjects = jsonAssetDeserializer.deserializeJson(updateJsonString).take(1)

        require(IdAmount > 0 &&
                listOfRelevantIDs.size == IdAmount &&
                listOfDataObjects.size == IdAmount)


        val desiredObject = listOfDataObjects[0]

        val entry = EntryTable(
            id = listOfRelevantIDs[0],
            dateDB = desiredObject.date,
            entryDB = desiredObject.title,
            timeMinutes = desiredObject.time
        )


        room_DAO.updateEntry(entry)

    }


    // UPDATE ENTRIES BULK
     suspend fun updateEntriesBenchmark_Room (IdAmount: Int) {

        var dbSize = countEntriesBenchmark_Room().toInt()

        var listOfIDs = accessListOfIdsBySize_Room(dbSize)

        var listOfRelevantIDs = listOfIDs.take(IdAmount)

        val updateJsonString = jsonAssetReader.loadJsonFromAssets("eventsForUpdate_A100000_S6.json")

        val listOfDataObjects = jsonAssetDeserializer.deserializeJson(updateJsonString).take(IdAmount)

        require(IdAmount > 0 &&
                listOfRelevantIDs.size == IdAmount &&
                listOfDataObjects.size == IdAmount)



        val entries: List<EntryTable> = listOfDataObjects.mapIndexed { index, event ->
            EntryTable(
                id = listOfRelevantIDs[index],
                dateDB = event.date,
                entryDB = event.title,
                timeMinutes = event.time
            )
        }

        room_DAO.updateEntries(entries)
    }



    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________

    // COUNT ALL ENTRIES
     suspend fun countEntriesBenchmark_Room (): Long {
        val count = room_DAO.countEntries()
        return count
    }


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________

    // GET ALL ENTRIES
     suspend fun getAllEntries_Room () {
        room_DAO.getAllEntriesBulk()
    }


    // GET ENTRY BY ID
     suspend fun getEntryByIdBenchmark_Room () {

        var dbSize = countEntriesBenchmark_Room().toInt()

        var listOfIDs = accessListOfIdsBySize_Room(dbSize)

        room_DAO.getSpecificEntry(listOfIDs[0])
    }



    // GET ENTRIES BY IDs BULK
     suspend fun getEntriesByIdsBenchmark_Room (IdVariant: Int) {

        var dbSize = countEntriesBenchmark_Room().toInt()

        var listOfIDs = accessListOfIdsBySize_Room(dbSize)
        var IdAmount = firstXamountIDs_Room(IdVariant)

        require(IdAmount <= listOfIDs.size) {
            "Requested $IdAmount IDs, but the file contains ${listOfIDs.size}"
        }

        var listOfDesiredIDs = listOfIDs.take(IdAmount)

        val results = room_DAO.getEntriesByIDs(listOfDesiredIDs)
        Log.d("Room_GET", "Requested $IdAmount, returned ${results.size}")
    }


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________

    // DELETE ENTRY BY ID
     suspend fun deleteEntryByIdBenchmark_Room () {

        var dbSize = countEntriesBenchmark_Room().toInt()

        var listOfIDs = accessListOfIdsBySize_Room(dbSize)

        val entry = EntryTable(
            id = listOfIDs[0],
            dateDB = null,
            entryDB = null,
            timeMinutes = null
        )

        room_DAO.deleteEntry(entry)

    }


    // DELETE ENTRIES BY IDs
     suspend fun deleteEntriesByIdsBenchmark_Room(IdVariant: Int) {

        var dbSize = countEntriesBenchmark_Room().toInt()

        var listOfIDs = accessListOfIdsBySize_Room(dbSize)
        val IdAmount = firstXamountIDs_Room(IdVariant)

        require(IdAmount <= listOfIDs.size)

        val listOfDesiredIDs = listOfIDs.take(IdAmount)

        val entries: List<EntryTable> = listOfDesiredIDs.mapIndexed { index, event ->
            EntryTable(
                id = listOfDesiredIDs[index],
                dateDB = null,
                entryDB = null,
                timeMinutes = null
            )
        }



        room_DAO.deleteEntries(entries)
    }


    // DELETE ALL ENTRIES
     suspend fun deleteAllEntriesBenchmark_Room () {

        room_DAO.deleteAllEntries()

    }


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // MEDIUM QUERIES


    // Find entries in date, order by time
     suspend fun findEntriesInSpecificDateBenchmarking_Room () {

        var date = "2026-10-05"

        var result = room_DAO.findEntriesInSpecificDate(date)
    }


    // Find entries in date range, order by time
     suspend fun findEntriesInDateRangeBenchmarking_Room () {

        var startDate = "2026-02-15"
        var endDate = "2026-10-05"

        var result = room_DAO.findEntriesInDateRange(startDate, endDate)
    }


    // FIND next X entries from date to date with reminder (not null)
     suspend fun findNextEntriesBenchmarking_Room () {

        var startDate = "2026-02-15"
        var endDate = "2026-10-05"
        var amount: Int = 15

        var result = room_DAO.findNextEntries(startDate, endDate, amount)
    }


    // Find entries with a specific reminder
     suspend fun findEntriesWithSpecificReminderBenchmarking_Room () {
        var specificReminder = "10 mins before"

        var result = room_DAO.findEntriesWithSpecificReminder(specificReminder)
    }


    // Find entries with any recurrence
     suspend fun findEntriesWithRecurranceBenchmarking_Room () {

        var result = room_DAO.findEntriesWithRecurrence()
    }


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // ADVANCED QUERIES


    // FIND Entries in Date Range with specific Reminder and specific Repeat1 or Repeat2
     suspend fun findEntriesInDateRangeReminderRepeat1OrRepeat2Benchmarking_Room () {

        var startDate = "2026-02-15"
        var endDate = "2026-10-05"
        var limit: Int = 15

        var specificReminder = "10 mins before"

        var specificRepeat1 = "Weekly"
        var specificRepeat2 = "Monthly"

        var result = room_DAO.findEntriesInDateRangeReminderRepeat1OrRepeat2(
            startDate,
            endDate,
            specificReminder,
            specificRepeat1,
            specificRepeat2,
            limit
        )
    }


    // FIND Entries with Reminder is Null and Repeat is Not Null + Limit + Offset
     suspend fun findEntriesReminderNullRepeatNotNullLimitOffsetBenchmarking_Room () {

        var limit: Int = 15
        var offset: Int = 10

        var result = room_DAO.findEntriesReminderNullRepeatNotNullLimitOffset(limit, offset)

    }


    // Find all repeat types and count them
     suspend fun countEntriesByRepeatTypeBenchmarking_Room () {

        var result = room_DAO.countEntriesByRepeatType()

    }


    // Earliest and latest event time among events in a date range with a specific reminder
     suspend fun findEarliestLatestEventTimesInRangeWithReminderBenchmarking_Room () {

        var startDate = "2026-02-15"
        var endDate = "2026-10-05"
        var specificReminder = "10 mins before"

        var result = room_DAO.findEarliestEventsInRangeWithReminder(
            startDate,
            endDate,
            specificReminder
        )

    }



    // Find all entries whose title contains a specified text fragment
    // and whose event time is later than a specified time.
     suspend fun findEntriesContainsSpecificTextTimeIsLaterThanSpecifiedTimeBenchmarking_Room () {

        var textFragment = "abc"
        var timeFloor = 500

        var result = room_DAO.findEntriesContainsSpecificTextTimeIsLaterThanSpecifiedTime(textFragment, timeFloor)

    }




}