package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.DAO.CBL_DAO
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.DAO.OB_DAO
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.data_mapping.CBL_Mapping
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.data_mapping.OB_Mapping
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.helper_classes.CouchbaseLite.CBL_EntryWithExtraData
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.helper_classes.ObjectBox.UpdateEntries_ObjectBox
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.JsonAssetDeserializer
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.JsonAssetReader
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.JsonIDsAssetDeserializer
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryOb_B
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.database_setup.CouchbaseLiteProvider
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.database_setup.ObjectBoxProvider
import com.couchbase.lite.MutableDocument
import java.lang.Long.toString

class CBL_ViewModel (application: Application) : AndroidViewModel(application) {


    private var jsonAssetReader =
        JsonAssetReader(application.applicationContext)

    private var jsonAssetDeserializer =
        JsonAssetDeserializer()

    private var cbl_Mapping =
        CBL_Mapping()

    private var cbl_DAO =
        CBL_DAO()

    private var jsonIDsAssetDeserializer =
        JsonIDsAssetDeserializer()



    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // HELPER FUNCTIONS


    fun accessListOfIdsBySize_CouchbaseLite(databaseSize: Int): List<Long> {
        require(databaseSize in listOf(100, 1000, 10000, 50000, 100000)) {
            "No ID dataset exists for database size $databaseSize"
        }

        val jsonString =
            jsonAssetReader.loadJsonFromAssets("IDs_A${databaseSize}.json")

        return jsonIDsAssetDeserializer.deserializeIDsJson(jsonString)
    }




    fun firstXamountIDs_CouchbaseLite (IdVariant: Int): Int {

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

    // RE-CREATE A FRESH DATABASE
    fun createFreshDatabase_CouchbaseLite () {

        CouchbaseLiteProvider.reset()

        cbl_DAO = CBL_DAO()
        cbl_Mapping = CBL_Mapping()


    }

    // *** INSERT DATA SET - INSERT FULL DATA SET
    fun insertDataSet_CouchbaseLite (variant: Int) {

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

        val listOfEntityDataObjects = cbl_Mapping.map(listOfDataObjects, 1L)

        var ids = cbl_DAO.insertDocuments(listOfEntityDataObjects)

    }


    // ***** BENCHMARKING - INSERT DATA SET - INSERT FULL DATA SET
    fun insertDataSetBenchmarking_CouchbaseLite (variant: Int): List<CBL_EntryWithExtraData> {

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

        val listOfEntityDataObjects = cbl_Mapping.map(listOfDataObjects, 1L)

        return listOfEntityDataObjects

        // var ids = cbl_DAO.insertDocuments(listOfEntityDataObjects)

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


    // INSERT ONE ENTRY
    fun insertMainEntryBenchmark_CouchbaseLite (): MutableDocument {

        val nextId = cbl_DAO.findNextEntryId()

            val entry = MutableDocument(nextId.toString())
                .setString("date", "2026-08-27")
                .setString("entry", "TyOk6EFsIWL6L3YOY15gwiS")
                .setInt("time_minutes", 1427)


        return entry


        // cbl_DAO.insertMainDoc(entry)

    }


    // *** CRUD - INSERT MAIN ENTRIES BULK
    // ALSO *** INSERT DATA SET - MAIN DATA ONLY
    fun insertMainDataSet_CouchbaseLite (variant: Int) {

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

        val listOfEntityDataObjects = cbl_Mapping.mapMainEntries(listOfDataObjects, 1L)

        cbl_DAO.insertMainDocs(listOfEntityDataObjects)

    }



    // ***** BENCHMARKING - CRUD - INSERT MAIN ENTRIES BULK
    // ALSO *** INSERT DATA SET - MAIN DATA ONLY
    fun insertMainDataSetBenchmark_CouchbaseLite (variant: Int): List<MutableDocument> {

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

        val firstId = cbl_DAO.findNextEntryId()

        val listOfDataObjects = jsonAssetDeserializer.deserializeJson(jsonString)

        val listOfEntityDataObjects = cbl_Mapping.mapMainEntries(
            listOfDataObjects,
            firstId
        )

        return listOfEntityDataObjects

        // cbl_DAO.insertMainDocs(listOfEntityDataObjects)

    }


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________

    // UPDATE ENTRY
    fun updateEntryBenchmark_CouchbaseLite (): MutableDocument {

        var dbSize = countEntriesBenchmark_CouchbaseLite().toInt()

        val IdAmount = 1

        var listOfIDs = accessListOfIdsBySize_CouchbaseLite(dbSize)

        var listOfRelevantIDs = listOfIDs.take(IdAmount)

        val updateJsonString = jsonAssetReader.loadJsonFromAssets("eventsForUpdate_A100000_S6.json")

        val listOfDataObjects = jsonAssetDeserializer.deserializeJson(updateJsonString).take(1)

        require(IdAmount > 0 &&
                listOfRelevantIDs.size == IdAmount &&
                listOfDataObjects.size == IdAmount)


        val desiredObject = listOfDataObjects[0]


        val entry = MutableDocument(toString(listOfRelevantIDs[0]))
            .setString("date", desiredObject.date)
            .setString("entry", desiredObject.title)
            .setInt("time_minutes", desiredObject.time)

        return entry


        // cbl_DAO.updateDocument(entry)

    }


    // UPDATE ENTRIES BULK
    fun updateEntriesBenchmark_CouchbaseLite (IdAmount: Int): List<MutableDocument> {

        var dbSize = countEntriesBenchmark_CouchbaseLite().toInt()

        var listOfIDs = accessListOfIdsBySize_CouchbaseLite(dbSize)


        var listOfRelevantIDs = listOfIDs.take(IdAmount)

        val updateJsonString = jsonAssetReader.loadJsonFromAssets("eventsForUpdate_A100000_S6.json")

        val listOfDataObjects = jsonAssetDeserializer.deserializeJson(updateJsonString).take(IdAmount)

        require(IdAmount > 0 &&
                listOfRelevantIDs.size == IdAmount &&
                listOfDataObjects.size == IdAmount)




        val entries = listOfDataObjects.mapIndexed { index, event ->
            MutableDocument(listOfRelevantIDs[index].toString())
                .setString("date", event.date)
                .setString("entry", event.title)
                .setInt("time_minutes", event.time)
        }

        return entries

        // cbl_DAO.updateDocuments(entries)
    }

    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________


    // COUNT ALL ENTRIES
    fun countEntriesBenchmark_CouchbaseLite (): Long {
        val count = cbl_DAO.countDocuments()
        return count
    }


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________


    // GET ALL ENTRIES
    fun getAllEntries_CouchbaseLite () {
        cbl_DAO.getAllDocuments()
    }

    // GET ENTRY BY ID
    fun getEntryByIdBenchmark_CouchbaseLite (): List<Long> {

        var dbSize = countEntriesBenchmark_CouchbaseLite().toInt()

        var listOfIDs = accessListOfIdsBySize_CouchbaseLite(dbSize)

        return listOfIDs

        // cbl_DAO.getDocumentById(toString(listOfIDs[0]))
    }

    // GET ENTRIES BY IDs BULK
    fun getEntriesByIdsBenchmark_CouchbaseLite (IdVariant: Int): List<String> {

        var dbSize = countEntriesBenchmark_CouchbaseLite().toInt()

        var listOfIDs = accessListOfIdsBySize_CouchbaseLite(dbSize)

        val listOfStringIDs: List<String> = listOfIDs.map {
            it.toString()
        }

        var IdAmount = firstXamountIDs_CouchbaseLite(IdVariant)

        require(IdAmount <= listOfIDs.size) {
            "Requested $IdAmount IDs, but the file contains ${listOfIDs.size}"
        }

        var listOfDesiredIDs = listOfStringIDs.take(IdAmount)

        return listOfDesiredIDs

        // val results = cbl_DAO.getDocumentsById(listOfDesiredIDs)
        // Log.d("CBL_GET", "Requested $IdAmount, returned ${results.size}")
    }


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________


    // DELETE ENTRY BY ID
    fun deleteEntryByIdBenchmark_CouchbaseLite (): String {

        var dbSize = countEntriesBenchmark_CouchbaseLite().toInt()

        var listOfIDs = accessListOfIdsBySize_CouchbaseLite(dbSize)

        return listOfIDs[0].toString()
        // cbl_DAO.deleteDocument(toString(listOfIDs[0]))

    }

    // DELETE ENTRIES BY IDs
    fun deleteEntriesByIdsBenchmark_CouchbaseLite(IdVariant: Int): List<String> {

        var dbSize = countEntriesBenchmark_CouchbaseLite().toInt()

        val listOfIDs = accessListOfIdsBySize_CouchbaseLite(dbSize)
        val IdAmount = firstXamountIDs_CouchbaseLite(IdVariant)

        require(IdAmount <= listOfIDs.size)

        val listOfDesiredIDs = listOfIDs.take(IdAmount)

        val listOfStringIDs: List<String> = listOfDesiredIDs.map {
            it.toString()
        }

        return listOfStringIDs

        // cbl_DAO.deleteDocuments(listOfStringIDs)
    }


    // DELETE ALL ENTRIES
    fun deleteAllEntriesBenchmark_CouchbaseLite () {

        cbl_DAO.deleteAllDocuments()

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
    fun findEntriesInSpecificDateBenchmarking_CouchbaseLite (): String {

        var date = "2026-10-05"

        return date
        // var result = cbl_DAO.findDocumentsInSpecificDate(date)
    }

    // Find entries in date range, order by time
    fun findEntriesInDateRangeBenchmarking_CouchbaseLite (): List<String> {

        var startDate = "2026-02-15"
        var endDate = "2026-10-05"

        var arguments = listOf(startDate, endDate)

        return arguments

        // var result = cbl_DAO.findDocumentsInDateRange(startDate, endDate)
    }


    // FIND next X entries from date to date with reminder (not null)
    fun findNextEntriesBenchmarking_CouchbaseLite (): List<Any> {

        var startDate = "2026-02-15"
        var endDate = "2026-10-05"
        var amount: Int = 15

        var arguments = listOf(startDate, endDate, amount)

        return arguments

        // var result = cbl_DAO.findNextDocument(startDate, endDate, amount)
    }


    // Find entries with a specific reminder
    fun findEntriesWithSpecificReminderBenchmarking_CouchbaseLite (): String {
        var specificReminder = "10 mins before"

        return specificReminder

        // var result = cbl_DAO.findDocumentsWithSpecificReminder(specificReminder)
    }

    // Find entries with any recurrence
    fun findEntriesWithRecurranceBenchmarking_CouchbaseLite () {

        // var result = cbl_DAO.findDocumentsWithReccurence()
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
    fun findEntriesInDateRangeReminderRepeat1OrRepeat2Benchmarking_CouchbaseLite (): List<Any> {

        var startDate = "2026-02-15"
        var endDate = "2026-10-05"
        var limit: Int = 15

        var specificReminder = "10 mins before"

        var specificRepeat1 = "Weekly"
        var specificRepeat2 = "Monthly"

        var arguments =
            listOf(startDate, endDate, specificReminder, specificRepeat1, specificRepeat2, limit)

        return arguments

        /*
        var result = cbl_DAO.findDocInDateRangeReminderRepeatOrRepeat(
            startDate,
            endDate,
            specificReminder,
            specificRepeat1,
            specificRepeat2,
            limit
        )

         */

    }



    // FIND Entries with Reminder is Null and Repeat is Not Null + Limit + Offset
    fun findEntriesReminderNullRepeatNotNullLimitOffsetBenchmarking_CouchbaseLite (): List<Int> {

        var limit: Int = 15
        var offset: Int = 10

        var arguments = listOf(limit, offset)

        return arguments

        // var result = cbl_DAO.findDocumentsReminderNullRepeatNotNullLimitOffset(limit, offset)

    }



    // Find all repeat types and count them
    fun countEntriesByRepeatTypeBenchmarking_CouchbaseLite () {

        var result = cbl_DAO.countDocumentsByRepeatType()

    }


    // Earliest and latest event time among events in a date range with a specific reminder
    fun findEarliestLatestEventTimesInRangeWithReminderBenchmarking_CouchbaseLite (): List<String> {

        var startDate = "2026-02-15"
        var endDate = "2026-10-05"
        var specificReminder = "10 mins before"

        var arguments = listOf(startDate, endDate, specificReminder)

        return arguments

        /*
        var result = cbl_DAO.findEarliestDocumentsInRangeWithReminder(
            startDate,
            endDate,
            specificReminder
        )

         */

    }


    // Find all entries whose title contains a specified text fragment
    // and whose event time is later than a specified time.
    fun findEntriesContainsSpecificTextTimeIsLaterThanSpecifiedTimeBenchmarking_CouchbaseLite (): List<Any> {

        var textFragment = "abc"
        var timeFloor = 500

        var arguments = listOf(textFragment, timeFloor)

        return arguments

        // var result = cbl_DAO.findDocTitleLikeTextTimeLater(textFragment, timeFloor)

    }





}