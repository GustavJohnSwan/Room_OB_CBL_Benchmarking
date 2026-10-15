package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.DAO.CBL_DAO
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.DAO.OB_DAO
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.data_mapping.CBL_Mapping
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.data_mapping.OB_Mapping
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

    private var updateEntries_ObjectBox =
        UpdateEntries_ObjectBox()

    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // HELPER FUNCTIONS


    fun accessListOfIdsByVariant_CouchbaseLite (variant: Int): List<Long> {

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


    fun createFreshDatabaseAndDataSet_CouchbaseLite (variant: Int) {

        CouchbaseLiteProvider.reset()

        cbl_DAO = CBL_DAO()
        cbl_Mapping = CBL_Mapping()

        insertDataSet_CouchbaseLite(variant)

    }


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

    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________


    fun createFreshDatabaseAndMainDataSet_CouchbaseLite (variant: Int) {

        CouchbaseLiteProvider.reset()

        cbl_DAO = CBL_DAO()
        cbl_Mapping = CBL_Mapping()

        insertMainDataSet_CouchbaseLite(variant)

    }


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


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________


    // INSERT ONE ENTRY
    fun insertEntryBenchmark_CouchbaseLite () {

            val entry = MutableDocument("1")
                .setString("date", "2026-08-27")
                .setString("entry", "TyOk6EFsIWL6L3YOY15gwiS")
                .setInt("time_minutes", 1427)


        cbl_DAO.insertMainDoc(entry)

    }



    // INSERT ENTRIES BULK
    fun insertEntriesBenchmark_CouchbaseLite (variant: Int) {

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


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________

    // UPDATE ENTRY
    fun updateEntryBenchmark_CouchbaseLite (variant: Int) {

        val IdAmount = 1

        var listOfIDs = accessListOfIdsByVariant_CouchbaseLite(variant)

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


        cbl_DAO.updateDocument(entry)

    }


    // UPDATE ENTRIES BULK
    fun updateEntriesBenchmark_CouchbaseLite (variant: Int, IdAmount: Int) {

        var listOfIDs = accessListOfIdsByVariant_CouchbaseLite(variant)

        var listOfRelevantIDs = listOfIDs.take(IdAmount)

        val updateJsonString = jsonAssetReader.loadJsonFromAssets("eventsForUpdate_A100000_S6.json")

        val listOfDataObjects = jsonAssetDeserializer.deserializeJson(updateJsonString).take(IdAmount)

        require(IdAmount > 0 &&
                listOfRelevantIDs.size == IdAmount &&
                listOfDataObjects.size == IdAmount)




        val desiredObject = listOfDataObjects[0]
        val entries = listOfDataObjects.mapIndexed { index, event ->
            MutableDocument(toString(listOfRelevantIDs[0]))
                .setString("date", desiredObject.date)
                .setString("entry", desiredObject.title)
                .setInt("time_minutes", desiredObject.time)
            }

        cbl_DAO.updateDocuments(entries)
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
    fun getEntryByIdBenchmark_CouchbaseLite (variant: Int) {

        var listOfIDs = accessListOfIdsByVariant_CouchbaseLite(variant)
        cbl_DAO.getDocumentById(toString(listOfIDs[0]))
    }

    // GET ENTRIES BY IDs BULK
    fun getEntriesByIdsBenchmark_CouchbaseLite (variant: Int, IdVariant: Int) {

        var listOfIDs = accessListOfIdsByVariant_CouchbaseLite(variant)

        val listOfStringIDs: List<String> = listOfIDs.map {
            it.toString()
        }

        var IdAmount = firstXamountIDs_CouchbaseLite(IdVariant)

        require(IdAmount <= listOfIDs.size) {
            "Requested $IdAmount IDs, but the file contains ${listOfIDs.size}"
        }

        var listOfDesiredIDs = listOfStringIDs.take(IdAmount)

        val results = cbl_DAO.getDocumentsById(listOfDesiredIDs)
        Log.d("OB_GET", "Requested $IdAmount, returned ${results.size}")
    }


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________


    // DELETE ENTRY BY ID
    fun deleteEntryByIdBenchmark_CouchbaseLite (variant: Int) {

        var listOfIDs = accessListOfIdsByVariant_CouchbaseLite(variant)
        cbl_DAO.deleteDocument(toString(listOfIDs[0]))

    }

    // DELETE ENTRIES BY IDs
    fun deleteEntriesByIdsBenchmark_CouchbaseLite(variant: Int, IdVariant: Int) {
        val listOfIDs = accessListOfIdsByVariant_CouchbaseLite(variant)
        val IdAmount = firstXamountIDs_CouchbaseLite(IdVariant)

        require(IdAmount <= listOfIDs.size)

        val listOfDesiredIDs = listOfIDs.take(IdAmount)

        val listOfStringIDs: List<String> = listOfDesiredIDs.map {
            it.toString()
        }

        cbl_DAO.deleteDocuments(listOfStringIDs)
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

    // Find entries in date, order by time in ObjectBox database
    fun findEntriesInSpecificDateBenchmarking_ObjectBox () {

        var date = "2026-10-05"

        var result = ob_DAO.findEntriesInSpecificDate(date)
    }




}