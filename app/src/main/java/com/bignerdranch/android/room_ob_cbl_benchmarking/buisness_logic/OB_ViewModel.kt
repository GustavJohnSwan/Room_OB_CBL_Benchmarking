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

    fun accessListOfIdsByVariant (variant: Int): List<Long> {

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



    fun firstXamountIDs (IdVariant: Int): Int {

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

    fun createFreshDatabaseAndDataSet (variant: Int) {

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



    fun createFreshDatabaseAndMainDataSet (variant: Int) {

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
    fun insertEntryBenchmark () {

        val entry = EntryOb_B(
            dateOb = "2026-08-27",
            entryOb = "TyOk6EFsIWL6L3YOY15gwiS",
            timeMinutesOb = 1427
        )

        ob_DAO.putMainEntry(entry)

    }


    // INSERT ENTRIES BULK
    fun insertEntriesBenchmark (variant: Int) {

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
    fun updateEntryBenchmark (variant: Int) {

        val IdAmount = 1

        var listOfIDs = accessListOfIdsByVariant(variant)

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
    fun updateEntriesBenchmark (variant: Int, IdAmount: Int) {

        var listOfIDs = accessListOfIdsByVariant(variant)

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
    fun countEntriesBenchmark (): Long {
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
    fun getAllEntries () {
        ob_DAO.getAllMainEntries()
    }

    // GET ENTRY BY ID
    fun getEntryByIdBenchmark (variant: Int) {

        var listOfIDs = accessListOfIdsByVariant(variant)

        ob_DAO.getMainEntry(listOfIDs[0])
    }



    // GET ENTRIES BY IDs BULK
    fun getEntriesByIdsBenchmark (variant: Int, IdVariant: Int) {

        var listOfIDs = accessListOfIdsByVariant(variant)
        var IdAmount = firstXamountIDs(IdVariant)

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
    fun deleteEntryByIdBenchmark (variant: Int) {

        var listOfIDs = accessListOfIdsByVariant(variant)

        ob_DAO.deleteMainEntry(listOfIDs[0])

    }

    // DELETE ENTRIES BY IDs
    fun deleteEntriesByIdsBenchmark(variant: Int, IdVariant: Int) {
        val listOfIDs = accessListOfIdsByVariant(variant)
        val IdAmount = firstXamountIDs(IdVariant)

        require(IdAmount <= listOfIDs.size)

        val listOfDesiredIDs = listOfIDs.take(IdAmount)
        ob_DAO.deleteMainEntries(listOfDesiredIDs)
    }

    // DELETE ALL ENTRIES
    fun deleteAllEntriesBenchmark () {

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



}