package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.DAO.OB_DAO
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.data_mapping.OB_Mapping
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.helper_classes.UpdateEntries_ObjectBox
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.JsonAssetDeserializer
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.JsonAssetReader
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.JsonIDsAssetDeserializer
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryOb_B
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.ExtraDataOb_B
import java.time.LocalDate


import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.GeneratedEvent
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.GeneratedExtraData
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.database_setup.ObjectBoxProvider

class BenchmarkViewModel (application: Application) : AndroidViewModel(application) {


    private var store = ObjectBoxProvider.get()
    private var repo = OB_DAO(store)

    private var jsonAssetReader =
        JsonAssetReader(application.applicationContext)

    private var jsonAssetDeserializer =
        JsonAssetDeserializer()

    private var ob_Mapping =
        OB_Mapping(store)

    private var ob_DAO =
        OB_DAO(store)

    var benchmarkStatus by mutableStateOf("Ready")
        private set


    private var jsonIDsAssetDeserializer =
        JsonIDsAssetDeserializer()

    private var updateEntries_ObjectBox =
        UpdateEntries_ObjectBox()



    fun testAllExtraDataUpdateTransitions() {

        // IMPORTANT:
        // This test clears the current ObjectBox data.
        ob_DAO.deleteAllEntries()


        // =========================================================
        // 1. CREATE FOUR ORIGINAL DATABASE ENTRIES
        // =========================================================

        // CASE 1: null -> null
        val entry1 = EntryOb_B(
            dateOb = "2026-01-01",
            entryOb = "CASE 1 ORIGINAL",
            timeMinutesOb = 100
        )


        // CASE 2: null -> ExtraData
        val entry2 = EntryOb_B(
            dateOb = "2026-01-02",
            entryOb = "CASE 2 ORIGINAL",
            timeMinutesOb = 200
        )


        // CASE 3: ExtraData -> null
        val entry3 = EntryOb_B(
            dateOb = "2026-01-03",
            entryOb = "CASE 3 ORIGINAL",
            timeMinutesOb = 300
        )

        entry3.extradataob_b.target = ExtraDataOb_B(
            reminderTypeOb = "10 mins before",
            repeatOb = "Weekly",
            repeatDetailsOb = "OLD CASE 3"
        )


        // CASE 4: ExtraData -> ExtraData
        val entry4 = EntryOb_B(
            dateOb = "2026-01-04",
            entryOb = "CASE 4 ORIGINAL",
            timeMinutesOb = 400
        )

        entry4.extradataob_b.target = ExtraDataOb_B(
            reminderTypeOb = "1 hour before",
            repeatOb = "Daily",
            repeatDetailsOb = "OLD CASE 4"
        )


        ob_DAO.putEntries(
            listOf(
                entry1,
                entry2,
                entry3,
                entry4
            )
        )


        // Save main IDs
        val entryIds = listOf(
            entry1.id,
            entry2.id,
            entry3.id,
            entry4.id
        )


        // Save original ExtraData IDs
        val oldCase3ExtraDataId =
            entry3.extradataob_b.targetId

        val oldCase4ExtraDataId =
            entry4.extradataob_b.targetId


        // =========================================================
        // 2. READ FRESH OBJECTS FROM DATABASE
        // =========================================================

        val originalEntries =
            ob_DAO.getEntriesByIDs(entryIds)


        // =========================================================
        // 3. CREATE UPDATE DATA FOR ALL FOUR CASES
        // =========================================================

        val updateData = listOf(

            // CASE 1:
            // null -> null
            GeneratedEvent(
                fixtureId = 1,
                date = "2026-02-01",
                title = "CASE 1 UPDATED",
                time = 101,
                extraData = null
            ),


            // CASE 2:
            // null -> ExtraData
            GeneratedEvent(
                fixtureId = 2,
                date = "2026-02-02",
                title = "CASE 2 UPDATED",
                time = 201,
                extraData = GeneratedExtraData(
                    reminderType = "At time of event",
                    repeatType = "Monthly",
                    repeatDetails = "NEW CASE 2"
                )
            ),


            // CASE 3:
            // ExtraData -> null
            GeneratedEvent(
                fixtureId = 3,
                date = "2026-02-03",
                title = "CASE 3 UPDATED",
                time = 301,
                extraData = null
            ),


            // CASE 4:
            // ExtraData -> ExtraData
            GeneratedEvent(
                fixtureId = 4,
                date = "2026-02-04",
                title = "CASE 4 UPDATED",
                time = 401,
                extraData = GeneratedExtraData(
                    reminderType = "1 day before",
                    repeatType = "Yearly",
                    repeatDetails = "NEW CASE 4"
                )
            )
        )


        // =========================================================
        // 4. RUN YOUR REAL UPDATE LOGIC
        // =========================================================

        val updateResult =
            updateEntries_ObjectBox.update(
                originalEntries,
                updateData
            )

        val updatedEntries =
            updateResult.first

        val extraDataIdsToDelete =
            updateResult.second


        ob_DAO.putEntries(
            updatedEntries,
            extraDataIdsToDelete
        )


        // =========================================================
        // 5. READ EVERYTHING FRESH FROM OBJECTBOX
        // =========================================================

        val results =
            ob_DAO.getEntriesByIDs(entryIds)


        val result1 = results[0]
        val result2 = results[1]
        val result3 = results[2]
        val result4 = results[3]


        // =========================================================
        // 6. VERIFY MAIN IDs WERE PRESERVED
        // =========================================================

        check(result1.id == entryIds[0])
        check(result2.id == entryIds[1])
        check(result3.id == entryIds[2])
        check(result4.id == entryIds[3])


        // =========================================================
        // CASE 1: null -> null
        // =========================================================

        check(result1.extradataob_b.target == null)

        Log.d(
            "OB_UPDATE_4_CASE_TEST",
            "PASS CASE 1: null -> null"
        )


        // =========================================================
        // CASE 2: null -> ExtraData
        // =========================================================

        val result2Extra =
            result2.extradataob_b.target

        check(result2Extra != null)

        check(
            result2Extra.reminderTypeOb ==
                    "At time of event"
        )

        check(
            result2Extra.repeatOb ==
                    "Monthly"
        )

        check(
            result2Extra.repeatDetailsOb ==
                    "NEW CASE 2"
        )

        // Must actually have been persisted
        check(result2Extra.id != 0L)

        Log.d(
            "OB_UPDATE_4_CASE_TEST",
            "PASS CASE 2: null -> ExtraData | new ExtraData ID=${result2Extra.id}"
        )


        // =========================================================
        // CASE 3: ExtraData -> null
        // =========================================================

        check(result3.extradataob_b.target == null)

        // Old ExtraData entity must actually be gone from DB
        check(
            ob_DAO.EDOBBox.get(
                oldCase3ExtraDataId
            ) == null
        )

        Log.d(
            "OB_UPDATE_4_CASE_TEST",
            "PASS CASE 3: ExtraData -> null | deleted ExtraData ID=$oldCase3ExtraDataId"
        )


        // =========================================================
        // CASE 4: ExtraData -> ExtraData
        // =========================================================

        val result4Extra =
            result4.extradataob_b.target

        check(result4Extra != null)

        // It should UPDATE the existing ExtraData,
        // rather than creating another one.
        check(
            result4Extra.id ==
                    oldCase4ExtraDataId
        )

        check(
            result4Extra.reminderTypeOb ==
                    "1 day before"
        )

        check(
            result4Extra.repeatOb ==
                    "Yearly"
        )

        check(
            result4Extra.repeatDetailsOb ==
                    "NEW CASE 4"
        )

        Log.d(
            "OB_UPDATE_4_CASE_TEST",
            "PASS CASE 4: ExtraData -> ExtraData | preserved ExtraData ID=${result4Extra.id}"
        )


        // =========================================================
        // FINAL RESULT
        // =========================================================

        Log.d(
            "OB_UPDATE_4_CASE_TEST",
            "================ ALL 4 UPDATE CASES PASSED ================"
        )
    }


    fun insertDataSet_ObjectBox(variant: Int) {

        when (variant) {
            1 -> {

                val existingEntries = ob_DAO.getAllEntriesBulk()

                Log.d(
                    "OB_RESET",
                    "BEFORE INSERT -> count=${existingEntries.size}, " +
                            "firstId=${existingEntries.firstOrNull()?.id}, " +
                            "lastId=${existingEntries.lastOrNull()?.id}"
                )

                val jsonString = jsonAssetReader.loadJsonFromAssets("events_A100_S1.json")

                val listOfDataObjects = jsonAssetDeserializer.deserializeJson(jsonString)

                val listOfEntityDataObjects = ob_Mapping.map(listOfDataObjects)

                var ids = ob_DAO.putEntries(listOfEntityDataObjects)

                Log.d("OB_TEST", "Inserted in ObjectBox ${listOfEntityDataObjects.count()} elements")

                Log.d(
                    "OB_RESET",
                    "AFTER FRESH INSERT -> First ID: ${ids.first()}"
                )

                Log.d(
                    "OB_RESET",
                    "AFTER FRESH INSERT -> Last ID: ${ids.last()}"
                )
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

    fun findEntriesById(IdListSize: Int) {

        // default
        var jsonString = jsonAssetReader.loadJsonFromAssets("IDs_A100.json")

        when (IdListSize) {
            1 -> jsonString = jsonAssetReader.loadJsonFromAssets("IDs_A100.json")
            2 -> jsonString = jsonAssetReader.loadJsonFromAssets("IDs_A1000.json")
            3 -> jsonString = jsonAssetReader.loadJsonFromAssets("IDs_A10000.json")
            4 -> jsonString = jsonAssetReader.loadJsonFromAssets("IDs_A50000.json")
            5 -> jsonString = jsonAssetReader.loadJsonFromAssets("IDs_A100000.json")
            else -> Log.d("ERROR", "ERROR in findEntriesById()")
        }
        //var jsonString = jsonAssetReader.loadJsonFromAssets("IDs_A100.json")

        var listOfIDs = jsonIDsAssetDeserializer.deserializeIDsJson(jsonString)

        Log.d("OB_TEST", "List of IDs :  ${listOfIDs}")

        var foundEntries = ob_DAO.getEntriesByIDs(listOfIDs)

        Log.d("OB_TEST", "Found ${foundEntries.count()} entries")

        foundEntries.forEach { entry ->

            Log.d(
                "OB_TEST",
                "Entry: id=${entry.id}, " +
                        "date=${entry.dateOb}, " +
                        "title=${entry.entryOb}, " +
                        "time=${entry.timeMinutesOb}"
            )

            val extraData = entry.extradataob_b.target

            if (extraData != null) {
                Log.d(
                    "OB_TEST",
                    "ExtraData: " +
                            "id=${extraData.id}, " +
                            "reminder=${extraData.reminderTypeOb}, " +
                            "repeat=${extraData.repeatOb}, " +
                            "repeatDetails=${extraData.repeatDetailsOb}"
                )
            } else {
                Log.d(
                    "OB_TEST",
                    "ExtraData: NONE"
                )
            }
            Log.d("OB_TEST", "_________________________________________________________________________________")
        }

    }





    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________

    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // CLEANUP updateEntriesById() function code, remove testing code
    fun updateEntriesById(IdAmount: Int) {
        val entryAmount = ob_DAO.countEntries().toInt()

        // safety net
        require(IdAmount <= entryAmount) {
            "Cannot update $IdAmount entries because database contains only $entryAmount entries"
        }

        var jsonString = jsonAssetReader.loadJsonFromAssets("IDs_A${entryAmount}.json")
        var listOfIDs = jsonIDsAssetDeserializer.deserializeIDsJson(jsonString)

        var listOfRelevantIDs = listOfIDs.take(IdAmount)

        //Log.d("OB_UPDATE_TEST", "List of IDs: $listOfRelevantIDs")
        // CHANGED: Keep simple summary log
        Log.d(
            "OB_UPDATE_TEST",
            "Selected ${listOfRelevantIDs.size} IDs for UPDATE"
        )
        // breakpoint

        val updateJsonString = jsonAssetReader.loadJsonFromAssets("eventsForUpdate_A100000_S6.json")

        // CHANGED:
        // Deserialize the update dataset AND only take as many
        // GeneratedEvents as we actually need for this test
        val listOfDataObjects =
            jsonAssetDeserializer
                .deserializeJson(updateJsonString)
                .take(IdAmount)

        //var listOfDataObjects = jsonAssetDeserializer.deserializeJson(updateJsonString)

        //Log.d("OB_UPDATE_TEST", "List of Data Objects: $listOfDataObjects")
        // breakpoint

        var listOfEntityDataObjects = ob_DAO.getEntriesByIDs(listOfRelevantIDs)

        // ============================================================
        // CHANGED: FULL DUMP OF ORIGINAL DATABASE ENTITIES
        // This MUST happen BEFORE update(), because update() modifies
        // these existing objects in memory.
        // ============================================================

        Log.d(
            "OB_UPDATE_DUMP",
            "================ BEFORE UPDATE ================"
        )

        Log.d(
            "OB_UPDATE_DUMP",
            "COUNT = ${listOfEntityDataObjects.size}"
        )

        // CHANGED:
        // Print ONE entity per Log.d().
        // This prevents Android from truncating one enormous log message.
        listOfEntityDataObjects.forEachIndexed { index, entry ->

            // CHANGED: Access related ExtraData entity, if it exists
            val extraData = entry.extradataob_b.target

            Log.d(
                "OB_UPDATE_DUMP",
                "[$index] " +
                        "ID=${entry.id} | " +
                        "date=${entry.dateOb} | " +
                        "title=${entry.entryOb} | " +
                        "time=${entry.timeMinutesOb} | " +
                        "extraID=${extraData?.id} | " +
                        "reminder=${extraData?.reminderTypeOb} | " +
                        "repeat=${extraData?.repeatOb} | " +
                        "repeatDetails=${extraData?.repeatDetailsOb}"
            )
        }

        Log.d(
            "OB_UPDATE_DUMP",
            "================ END BEFORE UPDATE ================"
        )




        //Log.d("OB_UPDATE_TEST", "List of Original Entities : $listOfEntityDataObjects")
        // breakpoint

        val updateResult =
            updateEntries_ObjectBox.update(
                listOfEntityDataObjects,
                listOfDataObjects
            )

        val updatedListOfEntityDataObjects =
            updateResult.first

        val extraDataIdsToDelete =
            updateResult.second


        // ============================================================
        // CHANGED: FULL DUMP AFTER VALUES WERE CHANGED,
        // BUT BEFORE putEntries()
        // ============================================================

        Log.d(
            "OB_UPDATE_DUMP",
            "================ AFTER CHANGES - BEFORE PUT ================"
        )

        Log.d(
            "OB_UPDATE_DUMP",
            "COUNT = ${updatedListOfEntityDataObjects.size}"
        )

        // CHANGED: Again print every entity separately
        updatedListOfEntityDataObjects.forEachIndexed { index, entry ->

            // CHANGED: Include ExtraData state after update logic
            val extraData = entry.extradataob_b.target

            Log.d(
                "OB_UPDATE_DUMP",
                "[$index] " +
                        "ID=${entry.id} | " +
                        "date=${entry.dateOb} | " +
                        "title=${entry.entryOb} | " +
                        "time=${entry.timeMinutesOb} | " +
                        "extraID=${extraData?.id} | " +
                        "reminder=${extraData?.reminderTypeOb} | " +
                        "repeat=${extraData?.repeatOb} | " +
                        "repeatDetails=${extraData?.repeatDetailsOb}"
            )
        }

        Log.d(
            "OB_UPDATE_DUMP",
            "================ END AFTER CHANGES - BEFORE PUT ================"
        )

        // Log.d("OB_UPDATE_TEST", "List of UPDATED Entities : $updatedListOfEntityDataObjects")
        // breakpoint

        ob_DAO.putEntries(
            updatedListOfEntityDataObjects,
            extraDataIdsToDelete
        )


        var listOfEntityDataObjectsUpdated = ob_DAO.getEntriesByIDs(listOfRelevantIDs)


        // ============================================================
        // CHANGED: FULL DATABASE DUMP AFTER putEntries()
        // ============================================================

        Log.d(
            "OB_UPDATE_DUMP",
            "================ AFTER PUT - READ FROM DATABASE ================"
        )

        Log.d(
            "OB_UPDATE_DUMP",
            "COUNT = ${listOfEntityDataObjectsUpdated.size}"
        )

        // CHANGED:
        // Print every entity retrieved back from ObjectBox,
        // including its ExtraData.
        listOfEntityDataObjectsUpdated.forEachIndexed { index, entry ->

            val extraData = entry.extradataob_b.target

            Log.d(
                "OB_UPDATE_DUMP",
                "[$index] " +
                        "ID=${entry.id} | " +
                        "date=${entry.dateOb} | " +
                        "title=${entry.entryOb} | " +
                        "time=${entry.timeMinutesOb} | " +
                        "extraID=${extraData?.id} | " +
                        "reminder=${extraData?.reminderTypeOb} | " +
                        "repeat=${extraData?.repeatOb} | " +
                        "repeatDetails=${extraData?.repeatDetailsOb}"
            )
        }

        Log.d(
            "OB_UPDATE_DUMP",
            "================ END AFTER PUT - READ FROM DATABASE ================"
        )


        // CHANGED: Clear end marker
        Log.d(
            "OB_UPDATE_TEST",
            "________________________________ END OF UPDATE TESTING ________________________________"
        )

        //Log.d("OB_UPDATE_TEST", "List of UPDATED Entities from database : $updatedListOfEntityDataObjects")
        //Log.d("OB_UPDATE_TEST", "________________________________ END OF UPDATE TESTING ________________________________")
        // breakpoint

        //val listOfEntityDataObjects = ob_Mapping.map(listOfDataObjects)

    }


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // MEDIUM QUERIES

    // Find entries in date, order by time in ObjectBox database
    fun findEntriesByDate() {
        var desiredEntries = ob_DAO.findEntriesInSpecificDate("2026-01-01")

        Log.d("OB_MEDIUM_QUERY_TEST", "Entries in desired date : 2026-01-01")
        Log.d("OB_MEDIUM_QUERY_TEST", "$desiredEntries")
    }

    // Find entries in date range, order by time in ObjectBox database
    fun findEntriesByDateRange() {
        var desiredEntries = ob_DAO.findEntriesInDateRange("2026-09-05", "2026-12-01")

        Log.d("OB_MEDIUM_QUERY_TEST", "Entries in desired range : 2026-09-05 - 2026-12-01")
        Log.d("OB_MEDIUM_QUERY_TEST", "$desiredEntries")
    }


    // Find entries in date range, order by time in ObjectBox database
    fun findNextEntryFromTodayToDate() {
        var todayDate = LocalDate.now().toString()
        var endDate = "2026-12-01"
        var amount: Long = 10
        var desiredEntry = ob_DAO.findNextEntry(todayDate, endDate, amount)

        Log.d("OB_MEDIUM_QUERY_TEST", "Next $amount entry/ies from today ${todayDate} to ${endDate}")
        desiredEntry.forEach { entry ->
            var reminder = entry.extradataob_b.target?.reminderTypeOb
            Log.d("OB_MEDIUM_QUERY_TEST", "Entry : $entry ||| Reminder : $reminder")
        }
    }


    // Find entries with a specific reminder
    fun findEntriesWithOneTypeOfReminder() {
        var desiredEntries = ob_DAO.findEntriesWithSpecificReminder()

        Log.d("OB_MEDIUM_QUERY_TEST", "Entries with 10 mins before reminder :")
        desiredEntries.forEach { entry ->
            var reminder = entry.extradataob_b.target?.reminderTypeOb
            Log.d("OB_MEDIUM_QUERY_TEST", "Entry : $entry ||| Reminder : $reminder")
        }

    }

    // Find entries with any recurrence
    fun findEntriesWithAnyRecurrence() {
        var desiredEntries = ob_DAO.findEntriesWithRecurrance()

        Log.d("OB_MEDIUM_QUERY_TEST", "Entries with any recurrence :")
        desiredEntries.forEach { entry ->
            var recurrence = entry.extradataob_b.target?.repeatOb
            Log.d("OB_MEDIUM_QUERY_TEST", "Entry : $entry ||| Recurrence : $recurrence")
        }
    }


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // ADVANCED QUERIES

    fun updateEntriesFromDateToDate() {
        var startDate = "2026-01-01"
        var endDate = "2026-02-01"

        val jsonString = jsonAssetReader.loadJsonFromAssets("eventsForUpdate_A100000_S6.json")

        val listOfDataObjects = jsonAssetDeserializer.deserializeJson(jsonString)

        var originalEntries = ob_DAO.findEntriesInDateRange(startDate, endDate)

        Log.d("OB_MEDIUM_QUERY_TEST", "Original entries :")
        originalEntries.forEach { entry ->
            var extraData = entry.extradataob_b.target
            Log.d("OB_MEDIUM_QUERY_TEST", "Entry : $entry ||| Extra Data : $extraData")
        }

        var desiredEntries = ob_DAO.findEntriesInDateRangeForUpdate(startDate, endDate)

        require(listOfDataObjects.size >= desiredEntries.size)

        val desiredUpdateData =
            listOfDataObjects.take(desiredEntries.size)

        val updateResult =
            updateEntries_ObjectBox.update(
                desiredEntries,
                desiredUpdateData
            )

        val updatedEntries = updateResult.first
        val extraDataIdsToDelete = updateResult.second

        ob_DAO.putEntries(
            updatedEntries,
            extraDataIdsToDelete
        )



        Log.d("OB_MEDIUM_QUERY_TEST", "Updated entries :")
        desiredEntries.forEach { entry ->
            var extraData = entry.extradataob_b.target
            Log.d("OB_MEDIUM_QUERY_TEST", "Entry : $entry ||| Extra Data : $extraData")
        }
    }









    // DELETE entries in date range, in ObjectBox database
    fun deleteEntriesFromDateToDate() {

        val startDate = "2026-01-01"
        val endDate = "2026-02-01"

        val originalEntries =
            ob_DAO.findEntriesInDateRange(startDate, endDate)

        val extraDataIdsBeforeDelete =
            originalEntries
                .map { it.extradataob_b.targetId }
                .filter { it != 0L }

        Log.d(
            "OB_MEDIUM_QUERY_TEST",
            "ExtraData entries associated with deleted entries: ${extraDataIdsBeforeDelete.size}"
        )

        Log.d(
            "OB_MEDIUM_QUERY_TEST",
            "Found Entries BEFORE delete: ${originalEntries.count()}"
        )

        Log.d(
            "OB_MEDIUM_QUERY_TEST",
            "Original entries:"
        )

        originalEntries.forEach { entry ->

            val extraData =
                entry.extradataob_b.target

            Log.d(
                "OB_MEDIUM_QUERY_TEST",
                "Entry: $entry ||| Extra Data: $extraData"
            )
        }


        // Delete
        ob_DAO.findEntriesInDateRangeForDelete(
            startDate,
            endDate
        )


        // Check EntryOb_B
        val leftOverEntries =
            ob_DAO.findEntriesInDateRange(
                startDate,
                endDate
            )

        Log.d(
            "OB_MEDIUM_QUERY_TEST",
            "Found Entries AFTER delete: ${leftOverEntries.count()}"
        )

        Log.d(
            "OB_MEDIUM_QUERY_TEST",
            "Entries after deletion:"
        )

        leftOverEntries.forEach { entry ->

            val extraData =
                entry.extradataob_b.target

            Log.d(
                "OB_MEDIUM_QUERY_TEST",
                "Entry: $entry ||| Extra Data: $extraData"
            )
        }


        // Check ExtraDataOb_B
        val extraDataAfterDelete =
            ob_DAO.EDOBBox.get(extraDataIdsBeforeDelete)

        Log.d(
            "OB_MEDIUM_QUERY_TEST",
            "ExtraData remaining AFTER delete: ${extraDataAfterDelete.size}"
        )

        extraDataAfterDelete.forEach { extraData ->

            Log.d(
                "OB_MEDIUM_QUERY_TEST",
                "ORPHAN ExtraData still exists: $extraData"
            )
        }
    }



    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________









    fun deleteEntriesById(IdAmount: Int) {
        val entryAmount = ob_DAO.countEntries().toInt()

        // safety net
        require(IdAmount <= entryAmount) {
            "Cannot delete $IdAmount entries because database contains only $entryAmount entries"
        }

        var jsonString = jsonAssetReader.loadJsonFromAssets("IDs_A${entryAmount}.json")
        var listOfIDs = jsonIDsAssetDeserializer.deserializeIDsJson(jsonString)

        var listOfRelevantIDs = listOfIDs.take(IdAmount)

        //Log.d("OB_UPDATE_TEST", "List of IDs: $listOfRelevantIDs")
        // CHANGED: Keep simple summary log
        Log.d(
            "OB_DELETE_DUMP",
            "Selected ${listOfRelevantIDs.size} IDs for DELETE"
        )
        // breakpoint

        val entriesBeforeDelete =
            ob_DAO.getEntriesByIDs(listOfRelevantIDs)

        val extraDataIDsBeforeDelete = entriesBeforeDelete
            .map { it.extradataob_b.targetId }
            .filter { it != 0L }


        Log.d(
            "OB_DELETE_DUMP",
            "ExtraData before delete = ${extraDataIDsBeforeDelete.size}"
        )


        ob_DAO.deleteEntries(listOfRelevantIDs)

        val entriesAfterDelete =
            ob_DAO.getEntriesByIDs(listOfRelevantIDs)

        val extraDataAfterDelete =
            ob_DAO.EDOBBox.get(extraDataIDsBeforeDelete)

        Log.d(
            "OB_DELETE_DUMP",
            "Entries remaining = ${entriesAfterDelete.size}"
        )

        Log.d(
            "OB_DELETE_DUMP",
            "ExtraData remaining = ${extraDataAfterDelete.size}"
        )



        var listOfEntityDataObjectsUpdated = ob_DAO.getEntriesByIDs(listOfRelevantIDs)


        // ============================================================
        // CHANGED: FULL DATABASE DUMP AFTER putEntries()
        // ============================================================

        Log.d(
            "OB_DELETE_DUMP",
            "================ AFTER DELETE - READ FROM DATABASE ================"
        )

        Log.d(
            "OB_DELETE_DUMP",
            "COUNT = ${listOfEntityDataObjectsUpdated.size}"
        )



        Log.d(
            "OB_DELETE_DUMP",
            "================ AFTER DELETE - READ FROM DATABASE ================"
        )


        // CHANGED: Clear end marker
        Log.d(
            "OB_DELETE_DUMP",
            "________________________________ END OF DELETE TESTING ________________________________"
        )

        //Log.d("OB_UPDATE_TEST", "List of UPDATED Entities from database : $updatedListOfEntityDataObjects")
        //Log.d("OB_UPDATE_TEST", "________________________________ END OF UPDATE TESTING ________________________________")
        // breakpoint

        //val listOfEntityDataObjects = ob_Mapping.map(listOfDataObjects)

    }

















    fun deleteAllEntries_ObjectBox() {
        ob_DAO.deleteAllEntries()
    }


    // remove dedicated testing log.d and functionality when no longer needed (the function its self is needed, but not all actions that are inside it)
    fun resetDataBase_ObjectBox() {

        benchmarkStatus = "Resetting ObjectBox database..."

        // Check what exists BEFORE reset
        val entriesBeforeReset =
            ob_DAO.getAllEntriesBulk()

        Log.d(
            "OB_RESET",
            "BEFORE RESET -> Entry count: ${entriesBeforeReset.size}"
        )

        Log.d(
            "OB_RESET",
            "BEFORE RESET -> Entry IDs: ${entriesBeforeReset.map { it.id }}"
        )


        // FULL RESET
        ObjectBoxProvider.reset(
            getApplication<Application>().applicationContext
        )

        Log.d(
            "OB_RESET",
            "Old ObjectBox database deleted and new database created"
        )


        // IMPORTANT:
        // Get the new BoxStore
        store =
            ObjectBoxProvider.get()

        // Recreate anything that used the OLD BoxStore
        ob_DAO =
            OB_DAO(store)

        ob_Mapping =
            OB_Mapping(store)


        // Check new database
        val entriesAfterReset =
            ob_DAO.getAllEntriesBulk()

        Log.d(
            "OB_RESET",
            "AFTER RESET -> Entry count: ${entriesAfterReset.size}"
        )

        benchmarkStatus = "ObjectBox database reset"
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

        ob_DAO.deleteEntries(entryIds)

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