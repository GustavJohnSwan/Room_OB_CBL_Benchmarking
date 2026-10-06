package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic

import android.app.Application
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
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________


    // INSERT ONE ENTRY
    fun insertEntryBenchmark () {

        val entry = EntryOb_B(
            dateOb = "2026-01-01",
            entryOb = "Example text 1",
            timeMinutesOb = 500
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

        val listOfEntityDataObjects = ob_Mapping.map(listOfDataObjects)

        ob_DAO.putMainEntries(listOfEntityDataObjects)

    }



}