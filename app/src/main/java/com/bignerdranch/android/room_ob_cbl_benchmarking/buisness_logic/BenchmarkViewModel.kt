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
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryOb_B
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.ExtraDataOb_B


class BenchmarkViewModel (application: Application) : AndroidViewModel(application) {


    private val store = ObjectBoxProvider.get()
    private val repo = OB_DAO(store)

    private val jsonAssetReader =
        JsonAssetReader(application.applicationContext)

    private val jsonAssetDeserializer =
        JsonAssetDeserializer()

    private val ob_Mapping =
        OB_Mapping(store)

    private val ob_DAO =
        OB_DAO(store)

    var benchmarkStatus by mutableStateOf("Ready")
        private set


    fun insertDataSet_ObjectBox(variant: Int) {

        when (variant) {
            1 -> {
                val jsonString = jsonAssetReader.loadJsonFromAssets("events_A100_S1.json")

                val listOfDataObjects = jsonAssetDeserializer.deserializeJson(jsonString)

                val listOfEntityDataObjects = ob_Mapping.map(listOfDataObjects)

                ob_DAO.putEntries(listOfEntityDataObjects)

                Log.d("OB_TEST", "Inserted in ObjectBox ${listOfEntityDataObjects.count()} elements")
            }
            2 -> {
                val jsonString = jsonAssetReader.loadJsonFromAssets("events_A1000_S2.json")

                val listOfDataObjects = jsonAssetDeserializer.deserializeJson(jsonString)

                val listOfEntityDataObjects = ob_Mapping.map(listOfDataObjects)

                ob_DAO.putEntries(listOfEntityDataObjects)

                Log.d("OB_TEST", "Inserted in ObjectBox ${listOfEntityDataObjects.count()} elements")
            }
            3 -> {
                val jsonString = jsonAssetReader.loadJsonFromAssets("events_A10000_S3.json")

                val listOfDataObjects = jsonAssetDeserializer.deserializeJson(jsonString)

                val listOfEntityDataObjects = ob_Mapping.map(listOfDataObjects)

                ob_DAO.putEntries(listOfEntityDataObjects)

                Log.d("OB_TEST", "Inserted in ObjectBox ${listOfEntityDataObjects.count()} elements")
            }
            4 -> {
                val jsonString = jsonAssetReader.loadJsonFromAssets("events_A50000_S4.json")

                val listOfDataObjects = jsonAssetDeserializer.deserializeJson(jsonString)

                val listOfEntityDataObjects = ob_Mapping.map(listOfDataObjects)

                ob_DAO.putEntries(listOfEntityDataObjects)

                Log.d("OB_TEST", "Inserted in ObjectBox ${listOfEntityDataObjects.count()} elements")
            }
            5 -> {
                val jsonString = jsonAssetReader.loadJsonFromAssets("events_A100000_S5.json")

                val listOfDataObjects = jsonAssetDeserializer.deserializeJson(jsonString)

                val listOfEntityDataObjects = ob_Mapping.map(listOfDataObjects)

                ob_DAO.putEntries(listOfEntityDataObjects)

                Log.d("OB_TEST", "Inserted in ObjectBox ${listOfEntityDataObjects.count()} elements")
            }
            else -> "Error in insertDataSet_ObjectBox()"
        }
    }

    fun deleteAllEntries_ObjectBox() {
        ob_DAO.deleteAllEntries()
    }

    fun findEntriesById() {

    }











    // THIS CODE WAS MOSTLY USED FOR READING, DE-SERIALIZING AND INSERTING JSON DATASET.
    // THEN IT WAS USED TO TEST ALL BASIC CRUD FUNCTIONS ON THIS DATASET
    fun loadDataSet() {

        // CLEARING THE DATABASE
        ob_DAO.deleteAllEntries()

        val allEntries = ob_DAO.getAllEntriesBulk()

        allEntries.forEach {
            val extraData = it.extradataob_b.target
            Log.d("OB_TEST", "Main data: $it | Extra data: $extraData")
        }


        // TEST 1 - INSERT ENTRY & DELETE ENTRY

        val testEntry0 = EntryOb_B(
            dateOb = "2026-08-28",
            entryOb = "Test event",
            timeMinutesOb = 780
        )

        val testExtraData = ExtraDataOb_B(
            reminderTypeOb = "10 mins before",
            repeatOb = "Weekly",
            repeatDetailsOb = "FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,WE,FR"
        )

        testEntry0.extradataob_b.target = testExtraData

        val entryId = ob_DAO.putEntry(testEntry0)

        val listOfAllObEntries2 = ob_DAO.getAllEntriesBulk()

        val oneEntry = ob_DAO.getSpecificEntryOb_B(entryId)


        val extraData = oneEntry?.extradataob_b?.target
        Log.d("OB_TEST", "oneEntry : Main data: $oneEntry | Extra data: $extraData")

        listOfAllObEntries2.forEach {
            val extraData = it.extradataob_b.target
            Log.d("OB_TEST", "Main data: $it | Extra data: $extraData")
        }

        ob_DAO.deleteEntry(entryId)

        val listOfAllObEntries3 = ob_DAO.getAllEntriesBulk()

        listOfAllObEntries3.forEach {
            val extraData = it.extradataob_b.target
            Log.d("OB_TEST", "Main data: $it | Extra data: $extraData")
        }

        Log.d("OB_TEST", "oneEntry : Main data: $oneEntry | Extra data: $extraData")





        // TEST 2 - INSERT MANY ENTRIES & DELETE MANY ENTRIES

        val testEntry1 = EntryOb_B(
            dateOb = "2026-08-28",
            entryOb = "Test event 1",
            timeMinutesOb = 600
        )

        val testEntry2 = EntryOb_B(
            dateOb = "2026-08-29",
            entryOb = "Test event 2",
            timeMinutesOb = 720
        )

        val testEntry3 = EntryOb_B(
            dateOb = "2026-08-30",
            entryOb = "Test event 3",
            timeMinutesOb = 840
        )

        val extraData2 = ExtraDataOb_B(
            reminderTypeOb = "10 mins before",
            repeatOb = "Weekly",
            repeatDetailsOb = "FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,WE,FR"
        )

        testEntry2.extradataob_b.target = extraData2

        val testEntries = listOf(
            testEntry1, // no extra data
            testEntry2, // has extra data
            testEntry3  // no extra data
        )

       val entryIds = ob_DAO.putEntries(testEntries)

        val listOfAllObEntries4 = ob_DAO.getAllEntriesBulk()

        listOfAllObEntries4.forEach {
            val extraData = it.extradataob_b.target
            Log.d("OB_TEST", "Main data: $it | Extra data: $extraData")
        }

        ob_DAO.deleteManyEntries(entryIds)

        val listOfAllObEntries5 = ob_DAO.getAllEntriesBulk()

        listOfAllObEntries5.forEach {
            val extraData = it.extradataob_b.target
            Log.d("OB_TEST", "Main data: $it | Extra data: $extraData")
        }






        // TEST 3 - INSERT BULK ENTRIES & DELETE BULK ENTRIES

        val jsonString1 = jsonAssetReader.loadJsonFromAssets("events_A100_S1.json")

        val listOfDataObjects1 = jsonAssetDeserializer.deserializeJson(jsonString1)

        val listOfEntityDataObjects1 = ob_Mapping.map(listOfDataObjects1)

        ob_DAO.putEntries(listOfEntityDataObjects1)

        val listOfAllObEntries1 = ob_DAO.getAllEntriesBulk()

        listOfAllObEntries1.forEach {
            val extraData = it.extradataob_b.target
            Log.d("OB_TEST", "Main data: $it | Extra data: $extraData")
        }

        ob_DAO.deleteAllEntries()

        val listOfAllObEntries6 = ob_DAO.getAllEntriesBulk()

        listOfAllObEntries6.forEach {
            val extraData = it.extradataob_b.target
            Log.d("OB_TEST", "Main data: $it | Extra data: $extraData")
        }






        // TEST 4 - UPDATE 1 ENTRY

        val testEntry = EntryOb_B(
            dateOb = "2026-08-28",
            entryOb = "Update test",
            timeMinutesOb = 600
        )

        val testExtra = ExtraDataOb_B(
            reminderTypeOb = "10 mins before",
            repeatOb = "Weekly",
            repeatDetailsOb = "FREQ=WEEKLY;INTERVAL=1"
        )

        testEntry.extradataob_b.target = testExtra

        val id = ob_DAO.putEntry(testEntry)


        // Read persisted entry
        val storedEntry = ob_DAO.getSpecificEntryOb_B(id)

        Log.d(
            "UPDATE_TEST",
            "BEFORE: ${storedEntry?.extradataob_b?.target}"
        )


        // Change ONLY existing ExtraData
        storedEntry?.extradataob_b?.target?.reminderTypeOb = "1 hour before"


        // Call your ORIGINAL function again
        if (storedEntry != null) {
            ob_DAO.putEntry(storedEntry)
        }


        // Read again from database
        val storedEntryAfter = ob_DAO.getSpecificEntryOb_B(id)

        Log.d(
            "UPDATE_TEST",
            "AFTER: ${storedEntryAfter?.extradataob_b?.target}"
        )








        // _________________________________________________________________________________________

        /*
        val jsonString = jsonAssetReader.loadJsonFromAssets("events_A100_S1.json")

        val listOfDataObjects = jsonAssetDeserializer.deserializeJson(jsonString)

        val listOfEntityDataObjects = ob_Mapping.map(listOfDataObjects)

        ob_DAO.deleteAllEntries()

        ob_DAO.putEntries(listOfEntityDataObjects)


        val listOfAllObEntries = ob_DAO.getAllEntriesBulk()

        listOfAllObEntries.forEach {
            val extraData = it.extradataob_b.target
            Log.d("OB_TEST", "Main data: $it | Extra data: $extraData")
        }

    }

         */








// _________________________________________________________________________________________________
        // Objectbox


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

        repo.putEntries(entries)

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
}