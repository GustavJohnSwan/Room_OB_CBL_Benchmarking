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

    private var db = AppDatabase.getDatabase(application)
    private var room_DAO = db.Room_DAO()

    private var jsonIDsAssetDeserializer =
        JsonIDsAssetDeserializer()

    private var jsonAssetReader =
        JsonAssetReader(application.applicationContext)

    private var jsonAssetDeserializer =
        JsonAssetDeserializer()

    private var room_Mapping =
        Room_Mapping()


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // HELPER FUNCTIONS

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

    private suspend fun insertDataSet_Room (variant: Int) {

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

    private suspend fun insertMainDataSet_Room (variant: Int) {

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
    private suspend fun insertEntryBenchmark_Room () {

        val entry = EntryTable(
            dateDB = "2026-08-27",
            entryDB = "TyOk6EFsIWL6L3YOY15gwiS",
            timeMinutes = 1427
        )

        room_DAO.insertMainEntry(entry)
    }


    // INSERT ENTRIES BULK
    private suspend fun insertEntriesBenchmark_Room (variant: Int) {

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
    private suspend fun updateEntryBenchmark_Room (variant: Int) {

        val IdAmount = 1

        var listOfIDs = accessListOfIdsByVariant_Room(variant)

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
    private suspend fun updateEntriesBenchmark_Room (variant: Int, IdAmount: Int) {

        var listOfIDs = accessListOfIdsByVariant_Room(variant)

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
    private suspend fun countEntriesBenchmark_Room (): Long {
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
    private suspend fun getAllEntries_Room () {
        room_DAO.getAllEntriesBulk()
    }


    // GET ENTRY BY ID
    private suspend fun getEntryByIdBenchmark_Room (variant: Int) {

        var listOfIDs = accessListOfIdsByVariant_Room(variant)

        room_DAO.getSpecificEntry(listOfIDs[0])
    }



    // GET ENTRIES BY IDs BULK
    private suspend fun getEntriesByIdsBenchmark_Room (variant: Int, IdVariant: Int) {

        var listOfIDs = accessListOfIdsByVariant_Room(variant)
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
    private suspend fun deleteEntryByIdBenchmark_Room (variant: Int) {

        var listOfIDs = accessListOfIdsByVariant_Room(variant)

        val entry = EntryTable(
            id = listOfIDs[0],
            dateDB = null,
            entryDB = null,
            timeMinutes = null
        )

        room_DAO.deleteEntry(entry)

    }


    // DELETE ENTRIES BY IDs
    private suspend fun deleteEntriesByIdsBenchmark_Room(variant: Int, IdVariant: Int) {
        var listOfIDs = accessListOfIdsByVariant_Room(variant)
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
    private suspend fun deleteAllEntriesBenchmark_Room () {

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
    private suspend fun findEntriesInSpecificDateBenchmarking_Room () {

        var date = "2026-10-05"

        var result = room_DAO.findEntriesInSpecificDate(date)
    }


    // Find entries in date range, order by time
    private suspend fun findEntriesInDateRangeBenchmarking_Room () {

        var startDate = "2026-02-15"
        var endDate = "2026-10-05"

        var result = room_DAO.findEntriesInDateRange(startDate, endDate)
    }


    // FIND next X entries from date to date with reminder (not null)
    private suspend fun findNextEntriesBenchmarking_Room () {

        var startDate = "2026-02-15"
        var endDate = "2026-10-05"
        var amount: Int = 15

        var result = room_DAO.findNextEntries(startDate, endDate, amount)
    }


    // Find entries with a specific reminder
    private suspend fun findEntriesWithSpecificReminderBenchmarking_Room () {
        var specificReminder = "10 mins before"

        var result = room_DAO.findEntriesWithSpecificReminder(specificReminder)
    }


    // Find entries with any recurrence
    private suspend fun findEntriesWithRecurranceBenchmarking_Room () {

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
    private suspend fun findEntriesInDateRangeReminderRepeat1OrRepeat2Benchmarking_Room () {

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
    private suspend fun findEntriesReminderNullRepeatNotNullLimitOffsetBenchmarking_Room () {

        var limit: Int = 15
        var offset: Int = 10

        var result = room_DAO.findEntriesReminderNullRepeatNotNullLimitOffset(limit, offset)

    }


    // Find all repeat types and count them
    private suspend fun countEntriesByRepeatTypeBenchmarking_Room () {

        var result = room_DAO.countEntriesByRepeatType()

    }


    // Earliest and latest event time among events in a date range with a specific reminder
    private suspend fun findEarliestLatestEventTimesInRangeWithReminderBenchmarking_Room () {

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
    private suspend fun findEntriesContainsSpecificTextTimeIsLaterThanSpecifiedTimeBenchmarking_Room () {

        var textFragment = "abc"
        var timeFloor = 500

        var result = room_DAO.findEntriesContainsSpecificTextTimeIsLaterThanSpecifiedTime(textFragment, timeFloor)

    }




}