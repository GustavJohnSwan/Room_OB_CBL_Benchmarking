package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.DAO.OB_DAO
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.data_mapping.OB_Mapping
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.JsonAssetDeserializer
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.JsonAssetReader


class BenchmarkViewModel (application: Application) : AndroidViewModel(application){

    private val jsonAssetReader =
        JsonAssetReader(application.applicationContext)

    private val jsonAssetDeserializer =
        JsonAssetDeserializer()

    private val ob_Mapping =
        OB_Mapping()

    fun loadDataSet() {
        val jsonString = jsonAssetReader.loadJsonFromAssets("events_A100_S1.json")

        val listOfDataObjects = jsonAssetDeserializer.deserializeJson(jsonString)

        val entityObject = ob_Mapping.map(listOfDataObjects)

    }






    var benchmarkStatus by mutableStateOf("Ready")
        private set


    //-------------------------------------------------------------------------
    // Objectbox
    private val store = ObjectBoxProvider.get()
    private val repo = OB_DAO(store)





























// _________________________________________________________________________________________________
        // test code


        /*
        // testing if JSON file read worked
        val text = json_data_tests(jsonString)
        // testing if deserialization worked
        Log.d("JsonDeserializer", "Count data objects in list : ${listOfDataObjects.size}")

        Log.d("JsonDeserializer", "First event: ${listOfDataObjects.first()}")
        Log.d("JsonDeserializer", "Last event: ${listOfDataObjects.last()}")

        val firstEvent = listOfDataObjects.first()

        Log.d("JsonDeserializer", "fixtureId: ${firstEvent.fixtureId}")
        Log.d("JsonDeserializer", "date: ${firstEvent.date}")
        Log.d("JsonDeserializer", "title: ${firstEvent.title}")
        Log.d("JsonDeserializer", "time: ${firstEvent.time}")

        val eventWithoutExtraData =
            listOfDataObjects.firstOrNull { it.extraData == null }

        Log.d(
            "JsonDeserializer",
            "Event without extraData: $eventWithoutExtraData"
        )



        val eventWithExtraData =
            listOfDataObjects.firstOrNull { it.extraData != null }

        Log.d(
            "JsonDeserializer",
            "Event with extraData: $eventWithExtraData"
        )




        val eventWithExtraData2 =
            listOfDataObjects.firstOrNull { it.extraData != null }

        val extraData = eventWithExtraData?.extraData

        Log.d("JsonDeserializer", "reminderType: ${extraData?.reminderType}")
        Log.d("JsonDeserializer", "repeatType: ${extraData?.repeatType}")
        Log.d("JsonDeserializer", "repeatDetails: ${extraData?.repeatDetails}")




        val recurringEvent =
            listOfDataObjects.firstOrNull {
                it.extraData?.repeatType != null
            }

        Log.d(
            "JsonDeserializer",
            "Recurring event: $recurringEvent"
        )



        val reminderEvent =
            listOfDataObjects.firstOrNull {
                it.extraData?.reminderType != null
            }

        Log.d(
            "JsonDeserializer",
            "Reminder event: $reminderEvent"
        )


         */








// _________________________________________________________________________________________________
    // old c=test code
    // generate random entries (right now two fillins)
    /*
    fun generateRandomEntries(): List<EntryOb_B> {
        val entries = listOf(
            EntryOb_B(
                dateOb = "2026-07-14",
                entryOb = "Example text 1",
                timeMinutesOb = 630
            ),

            EntryOb_B(
                dateOb = "2026-07-14",
                entryOb = "Example text 2",
                timeMinutesOb = 90
            )
        )
        return entries
    }

     */

    // INSERT BULK
    // date format YYYY-MM-DD
    // time format formula : H*60+M --> 10:30 --> 10*60+30 = 630

    /*
    fun insertEntryBulk() {
        val entries = generateRandomEntries()

        Log.d(
            "OB_BENCHMARK",
            "About to insert ${entries.size} entries"
        )

        entries.forEach { entry ->
            Log.d(
                "OB_ADDED",
                "Before insert: $entry"
            )
        }

        repo.insertEntriesBulk(entries)

        entries.forEach { entry ->
            Log.d(
                "OB_ADDED",
                "Inserted: $entry"
            )
        }

        // BULK GET entries
        val allEntries = repo.getAllEntriesBulk()

        Log.d(
            "OB_DATABASE",
            "Current database contains ${allEntries.size} entries"
        )

        allEntries.forEach { entry ->
            Log.d(
                "OB_DATABASE",
                "Stored: $entry"
            )
        }
    }

     */

    /*
        fun universalPlaceholder() {


            val user = User(name = "Gatis")
            val household = Household(
                address = "Test address",
                color = "Blue",
                stories = 2
            )

            val userId = repo.insertUser(user)
            val houseHoldId = repo.insertHousehold(household)

            val allUsers = repo.getAllUsers()
            val allHouseholds = repo.getAllHouseholds()

            val specificUser = repo.getSpecificUser(userId)

            val usersStartingWithG = repo.getUsersType1()

            val householdCount = repo.countHouseholds()


            Log.d("OB_TEST", "Inserted user id: $userId")
            //Log.d("OB_TEST", "Inserted household id: $householdId")
            Log.d("OB_TEST", "All users: $allUsers")
            Log.d("OB_TEST", "All households: $allHouseholds")
            Log.d("OB_TEST", "Specific user: $specificUser")
            Log.d("OB_TEST", "Users starting with G: $usersStartingWithG")
            Log.d("OB_TEST", "Household count: $householdCount")
        }

     */
}