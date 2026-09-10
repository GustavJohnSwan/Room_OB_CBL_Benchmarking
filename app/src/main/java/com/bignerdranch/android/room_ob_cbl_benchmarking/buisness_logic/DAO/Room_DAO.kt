package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryTable

@Dao
interface Room_DAO {

    // currently just copied 3 functions from calendar app. Need to test basic insert, query and delete
    @Insert
    suspend fun insert_IntoEntryTable(entryTable: EntryTable): Long

    @Query("SELECT * FROM EntryTable")
    suspend fun get_AllEntries(): List<EntryTable>

    @Delete
    suspend fun delete_Entry(entry: EntryTable)


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // Room CRUD

    // INSERT BULK entries
    @Insert
    suspend fun insertEntries(entries: List<EntryTable>): List<Long>

    // UPDATE ENTRY if it exists
    @Update
    suspend fun updateEntry(entry: EntryTable)

    // UPDATE ENTRIES if it exists
    @Update
    suspend fun updateEntries(entries: List<EntryTable>)

    // UPDATE ENTRY if it exists, otherwise INSERT IT
    @Upsert
    suspend fun insertOrUpdateEntry(entry: EntryTable): Long

    // UPDATE ENTRIES if it exists, otherwise INSERT THEM
    @Upsert
    suspend fun insertOrUpdateEntries(entries: List<EntryTable>): List<Long>



    // COUNT all entries
    @Query("SELECT COUNT(id) FROM EntryTable")
    suspend fun countEntries(): Long



    // GET BULK
    @Query("SELECT * FROM EntryTable")
    suspend fun getAllEntriesBulk(): List<EntryTable>

    // GET ENTRY BY ID
    @Query("SELECT * FROM EntryTable WHERE id = :entryId")
    suspend fun getSpecificEntry(entryId: Long): EntryTable?

    // GET ENTRIES BY IDs
    @Query("SELECT * FROM EntryTable WHERE id IN (:entryId)")
    suspend fun getEntriesByIDs(entryId: List<Long>): List<EntryTable>



    // DELETE ENTRY
    @Delete
    suspend fun deleteEntry(entry: EntryTable): Int

    // DELETE ENTRIES
    @Delete
    suspend fun deleteEntries(entries: List<EntryTable>): Int






}