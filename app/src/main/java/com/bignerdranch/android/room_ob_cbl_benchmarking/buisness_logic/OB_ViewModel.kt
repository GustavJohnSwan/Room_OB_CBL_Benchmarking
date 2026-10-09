package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.DAO.OB_DAO
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.data_mapping.OB_Mapping
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.helper_classes.ObjectBox.UpdateEntries_ObjectBox
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.JsonAssetDeserializer
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.JsonAssetReader
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.JsonIDsAssetDeserializer
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryOb_B
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.database_setup.ObjectBoxProvider

class OB_ViewModel (application: Application) : AndroidViewModel(application) {

    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    private var store = ObjectBoxProvider.get()

    private var jsonAssetReader =
        JsonAssetReader(application.applicationContext)

    private var jsonAssetDeserializer =
        JsonAssetDeserializer()

    private var ob_Mapping =
        OB_Mapping(store)

    private var ob_DAO =
        OB_DAO(store)

    private var jsonIDsAssetDeserializer =
        JsonIDsAssetDeserializer()

    private var updateEntries_ObjectBox =
        UpdateEntries_ObjectBox()

    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // HELPER FUNCTIONS

    fun accessListOfIdsByVariant_ObjectBox (variant: Int): List<Long> {

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



    fun firstXamountIDs_ObjectBox (IdVariant: Int): Int {

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

    fun createFreshDatabaseAndDataSet_ObjectBox (variant: Int) {

        ObjectBoxProvider.reset(
            getApplication<Application>().applicationContext
        )

        store = ObjectBoxProvider.get()
        ob_DAO = OB_DAO(store)
        ob_Mapping = OB_Mapping(store)

        insertDataSet_ObjectBox(variant)

    }

    fun insertDataSet_ObjectBox (variant: Int) {

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

        val listOfEntityDataObjects = ob_Mapping.map(listOfDataObjects)

        var ids = ob_DAO.putEntries(listOfEntityDataObjects)

    }


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________



    fun createFreshDatabaseAndMainDataSet_ObjectBox (variant: Int) {

        ObjectBoxProvider.reset(
            getApplication<Application>().applicationContext
        )

        store = ObjectBoxProvider.get()
        ob_DAO = OB_DAO(store)
        ob_Mapping = OB_Mapping(store)

        insertMainDataSet_ObjectBox(variant)

    }


    fun insertMainDataSet_ObjectBox (variant: Int) {

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

        val listOfEntityDataObjects = ob_Mapping.mapMainEntries(listOfDataObjects)

        var ids = ob_DAO.putEntries(listOfEntityDataObjects)

    }




    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________


    // INSERT ONE ENTRY
    fun insertEntryBenchmark_ObjectBox () {

        val entry = EntryOb_B(
            dateOb = "2026-08-27",
            entryOb = "TyOk6EFsIWL6L3YOY15gwiS",
            timeMinutesOb = 1427
        )

        ob_DAO.putMainEntry(entry)

    }


    // INSERT ENTRIES BULK
    fun insertEntriesBenchmark_ObjectBox (variant: Int) {

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

        val listOfEntityDataObjects = ob_Mapping.mapMainEntries(listOfDataObjects)

        ob_DAO.putMainEntries(listOfEntityDataObjects)

    }


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________

    // UPDATE ENTRY
    fun updateEntryBenchmark_ObjectBox (variant: Int) {

        val IdAmount = 1

        var listOfIDs = accessListOfIdsByVariant_ObjectBox(variant)

        var listOfRelevantIDs = listOfIDs.take(IdAmount)

        val updateJsonString = jsonAssetReader.loadJsonFromAssets("eventsForUpdate_A100000_S6.json")

        val listOfDataObjects = jsonAssetDeserializer.deserializeJson(updateJsonString).take(1)

        require(IdAmount > 0 &&
                listOfRelevantIDs.size == IdAmount &&
                listOfDataObjects.size == IdAmount)


        val desiredObject = listOfDataObjects[0]

        val entry = EntryOb_B(
                id = listOfRelevantIDs[0],
                dateOb = desiredObject.date,
                entryOb = desiredObject.title,
                timeMinutesOb = desiredObject.time
            )


        ob_DAO.updateEntry(entry)

    }




    // UPDATE ENTRIES BULK
    fun updateEntriesBenchmark_ObjectBox (variant: Int, IdAmount: Int) {

        var listOfIDs = accessListOfIdsByVariant_ObjectBox(variant)

        var listOfRelevantIDs = listOfIDs.take(IdAmount)

        val updateJsonString = jsonAssetReader.loadJsonFromAssets("eventsForUpdate_A100000_S6.json")

        val listOfDataObjects = jsonAssetDeserializer.deserializeJson(updateJsonString).take(IdAmount)

        require(IdAmount > 0 &&
                listOfRelevantIDs.size == IdAmount &&
                listOfDataObjects.size == IdAmount)



        val entries = listOfDataObjects.mapIndexed { index, event ->
            EntryOb_B(
                id = listOfRelevantIDs[index],
                dateOb = event.date,
                entryOb = event.title,
                timeMinutesOb = event.time
            )
        }

        ob_DAO.updateEntries(entries)
    }


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________

    // COUNT ALL ENTRIES
    fun countEntriesBenchmark_ObjectBox (): Long {
        val count = ob_DAO.countEntries()
        return count
    }

    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________

    // GET ALL ENTRIES
    fun getAllEntries_ObjectBox () {
        ob_DAO.getAllMainEntries()
    }

    // GET ENTRY BY ID
    fun getEntryByIdBenchmark_ObjectBox (variant: Int) {

        var listOfIDs = accessListOfIdsByVariant_ObjectBox(variant)

        ob_DAO.getMainEntry(listOfIDs[0])
    }



    // GET ENTRIES BY IDs BULK
    fun getEntriesByIdsBenchmark_ObjectBox (variant: Int, IdVariant: Int) {

        var listOfIDs = accessListOfIdsByVariant_ObjectBox(variant)
        var IdAmount = firstXamountIDs_ObjectBox(IdVariant)

        require(IdAmount <= listOfIDs.size) {
            "Requested $IdAmount IDs, but the file contains ${listOfIDs.size}"
        }

        var listOfDesiredIDs = listOfIDs.take(IdAmount)

        val results = ob_DAO.getMainEntries(listOfDesiredIDs)
        Log.d("OB_GET", "Requested $IdAmount, returned ${results.size}")
    }

    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________

    // DELETE ENTRY BY ID
    fun deleteEntryByIdBenchmark_ObjectBox (variant: Int) {

        var listOfIDs = accessListOfIdsByVariant_ObjectBox(variant)

        ob_DAO.deleteMainEntry(listOfIDs[0])

    }

    // DELETE ENTRIES BY IDs
    fun deleteEntriesByIdsBenchmark_ObjectBox(variant: Int, IdVariant: Int) {
        val listOfIDs = accessListOfIdsByVariant_ObjectBox(variant)
        val IdAmount = firstXamountIDs_ObjectBox(IdVariant)

        require(IdAmount <= listOfIDs.size)

        val listOfDesiredIDs = listOfIDs.take(IdAmount)
        ob_DAO.deleteMainEntries(listOfDesiredIDs)
    }

    // DELETE ALL ENTRIES
    fun deleteAllEntriesBenchmark_ObjectBox () {

        ob_DAO.deleteAllMainEntries()

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

    // Find entries in date, order by time in ObjectBox database
    fun findEntriesInSpecificDateBenchmarking_ObjectBox () {

        var date = "2026-10-05"

        var result = ob_DAO.findEntriesInSpecificDate(date)
    }


    // Find entries in date range, order by time in ObjectBox database
    fun findEntriesInDateRangeBenchmarking_ObjectBox () {

        var startDate = "2026-02-15"
        var endDate = "2026-10-05"

        var result = ob_DAO.findEntriesInDateRange(startDate, endDate)
    }

    // FIND next X entries from date to date with reminder (not null)
    fun findNextEntriesBenchmarking_ObjectBox () {

        var startDate = "2026-02-15"
        var endDate = "2026-10-05"
        var amount: Long = 15

        var result = ob_DAO.findNextEntries(startDate, endDate, amount)
    }

    // Find entries with a specific reminder
    fun findEntriesWithSpecificReminderBenchmarking_ObjectBox () {
        var specificReminder = "10 mins before"

        var result = ob_DAO.findEntriesWithSpecificReminder(specificReminder)
    }

    // Find entries with any recurrence
    fun findEntriesWithRecurranceBenchmarking_ObjectBox () {

        var result = ob_DAO.findEntriesWithRecurrance()
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
    fun findEntriesInDateRangeReminderRepeat1OrRepeat2Benchmarking_ObjectBox () {

        var startDate = "2026-02-15"
        var endDate = "2026-10-05"
        var limit: Long = 15

        var specificReminder = "10 mins before"

        var specificRepeat1 = "Weekly"
        var specificRepeat2 = "Monthly"

        var result = ob_DAO.findEntriesInDateRangeReminderRepeat1OrRepeat2(
            startDate,
            endDate,
            specificReminder,
            specificRepeat1,
            specificRepeat2,
            limit
            )
    }



    // FIND Entries with Reminder is Null and Repeat is Not Null + Limit + Offset
    fun findEntriesReminderNullRepeatNotNullLimitOffsetBenchmarking_ObjectBox () {

        var limit: Long = 15
        var offset: Long = 10

        var result = ob_DAO.findEntriesReminderNullRepeatNotNullLimitOffset(limit, offset)

    }



    // Find all repeat types and count them
    fun countEntriesByRepeatTypeBenchmarking_ObjectBox () {

        var result = ob_DAO.countEntriesByRepeatType()

    }


    // Earliest and latest event time among events in a date range with a specific reminder
    fun findEarliestLatestEventTimesInRangeWithReminderBenchmarking_ObjectBox () {

        var startDate = "2026-02-15"
        var endDate = "2026-10-05"
        var specificReminder = "10 mins before"

        var result = ob_DAO.findEarliestLatestEventTimesInRangeWithReminder(
            startDate,
            endDate,
            specificReminder
        )

    }


    // Find all entries whose title contains a specified text fragment
    // and whose event time is later than a specified time.
    fun findEntriesContainsSpecificTextTimeIsLaterThanSpecifiedTimeBenchmarking_ObjectBox () {

        var textFragment = "abc"
        var timeFloor = 500

        var result = ob_DAO.findEntriesContainsSpecificTextTimeIsLaterThanSpecifiedTime(textFragment, timeFloor)

    }



}