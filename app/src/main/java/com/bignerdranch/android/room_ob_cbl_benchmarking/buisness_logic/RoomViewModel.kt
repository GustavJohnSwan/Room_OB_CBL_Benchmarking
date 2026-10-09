package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.DAO.Room_DAO
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.JsonAssetDeserializer
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.JsonAssetReader
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.JsonIDsAssetDeserializer
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryTable
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.database_setup.AppDatabase

import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.DAO.OB_DAO
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.data_mapping.OB_Mapping
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.data_mapping.Room_Mapping
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RoomViewModel (application: Application) : AndroidViewModel(application) {

    private var db = AppDatabase.getDatabase(application)
    private var Room_DAO = db.Room_DAO()

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

    fun createFreshDatabaseAndDataSet_Room (variant: Int) {

        viewModelScope.launch(Dispatchers.IO) {
            db = AppDatabase.reset(
                getApplication<Application>().applicationContext
            )
            Room_DAO = db.Room_DAO()

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

        var ids = Room_DAO.insertEntries(listOfEntityDataObjects)

    }


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________

    fun createFreshDatabaseAndMainDataSet_Room (variant: Int) {

        viewModelScope.launch(Dispatchers.IO) {
            db = AppDatabase.reset(
                getApplication<Application>().applicationContext
            )
            Room_DAO = db.Room_DAO()

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

        var ids = Room_DAO.insertMainEntries(listOfEntityDataObjects)

    }

    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________


    // INSERT ONE ENTRY
    fun insertEntryBenchmark_Room () {

    }

}