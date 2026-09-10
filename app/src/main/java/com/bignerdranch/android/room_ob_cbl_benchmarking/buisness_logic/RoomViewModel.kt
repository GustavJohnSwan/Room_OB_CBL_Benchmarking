package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.DAO.Room_DAO
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryTable
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.database_setup.AppDatabase




class RoomViewModel (application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val Room_DAO = db.Room_DAO()

    suspend fun insertEntry_Room() {
        val newEntry = EntryTable(
            dateDB = "2026-10-10",
            entryDB = "Entry Text",
            timeMinutes = 500
        )

        val entryId = Room_DAO.insert_IntoEntryTable(newEntry).toInt()
        Log.d("Room_CRUD_TEST", "Inserted Entry ID : $entryId")
    }

    suspend fun getAllEntry_Room() {
        val allEntries = Room_DAO.get_AllEntries()

        allEntries.forEach { entry ->
            Log.d("Room_CRUD_TEST", "Room Entry : $entry")
        }
    }

    suspend fun deleteEntry_Room() {
        val allEntries = Room_DAO.get_AllEntries()
        Room_DAO.delete_Entry(allEntries[0])
    }
}