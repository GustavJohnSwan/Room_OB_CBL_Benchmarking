package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.helper_classes.Room.MinMaxTimeResult
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.helper_classes.Room.RepeatTypeCountResult
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


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // Medium Queries

    // Find entries in date, order by time in Room database
    @Query("SELECT * FROM EntryTable WHERE date = :entryDate ORDER BY time_minutes ASC")
    suspend fun findEntriesInSpecificDate(entryDate: String): List<EntryTable>

    // Find entries in date range, order by time in Room database
    @Query("SELECT * FROM EntryTable WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC, time_minutes ASC")
    suspend fun findEntriesInDateRange(startDate: String, endDate: String): List<EntryTable>

    // Find next X entries from date to date with reminder (not null) in Room database
    @Query("SELECT EntryTable.* FROM EntryTable INNER JOIN ExtraDataTable ON EntryTable.id = ExtraDataTable.entry_id WHERE EntryTable.date BETWEEN :startDate AND :endDate AND ExtraDataTable.reminder_type IS NOT NULL ORDER BY EntryTable.date ASC, EntryTable.time_minutes ASC LIMIT :nextAmount")
    suspend fun findNextEntries(startDate: String, endDate: String, nextAmount: Int): List<EntryTable>

    // Find entries with a specific reminder
    @Query("SELECT EntryTable.* FROM EntryTable INNER JOIN ExtraDataTable ON EntryTable.id = ExtraDataTable.entry_id WHERE ExtraDataTable.reminder_type = :desiredReminderType")
    suspend fun findEntriesWithSpecificReminder(desiredReminderType: String): List<EntryTable>

    // Find entries with any recurrence
    @Query("SELECT EntryTable.* FROM EntryTable INNER JOIN ExtraDataTable ON EntryTable.id = ExtraDataTable.entry_id WHERE ExtraDataTable.repeat IS NOT NULL")
    suspend fun findEntriesWithRecurrence(): List<EntryTable>


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // Advanced Queries


    // FIND Entries in Date Range with specific Reminder and specific Repeat1 or Repeat2
    @Query("SELECT EntryTable.* FROM EntryTable " +
            "INNER JOIN ExtraDataTable ON EntryTable.id = ExtraDataTable.entry_id " +
            "WHERE EntryTable.date BETWEEN :startDate AND :endDate " +
            "AND ExtraDataTable.reminder_type = :specificReminder " +
            "AND (" +
            "ExtraDataTable.repeat = :specificRepeat1 " +
            "OR ExtraDataTable.repeat = :specificRepeat2" +
            ") " +
            "ORDER BY EntryTable.date, EntryTable.time_minutes ASC " +
            "LIMIT :amount ")
    suspend fun findEntriesInDateRangeReminderRepeat1OrRepeat2(
        startDate: String,
        endDate: String,
        specificReminder: String,
        specificRepeat1: String,
        specificRepeat2: String,
        amount: Int
        ): List<EntryTable>


    // FIND Entries with Reminder is Null and Repeat is Not Null + Limit + Offset
    @Query("SELECT EntryTable.* FROM EntryTable " +
            "INNER JOIN ExtraDataTable ON EntryTable.id = ExtraDataTable.entry_id " +
            "WHERE ExtraDataTable.reminder_type IS NULL " +
            "AND ExtraDataTable.repeat IS NOT NULL " +
            "ORDER BY EntryTable.date ASC, " +
            "EntryTable.time_minutes ASC, " +
            "EntryTable.id ASC " +
            "LIMIT :limit " +
            "OFFSET :offset")
    suspend fun findEntriesReminderNullRepeatNotNullLimitOffset(
        limit: Int,
        offset: Int
    ): List<EntryTable>


    // Find all repeat types and count them
    @Query("SELECT ExtraDataTable.repeat, COUNT(repeat) AS repeatCount " +
            "FROM ExtraDataTable " +
            "GROUP BY repeat " +
            "ORDER BY ExtraDataTable.repeat")
    suspend fun countEntriesByRepeatType(): List<RepeatTypeCountResult>


    // Earliest and latest event time among events in a date range with a specific reminder
    @Query("SELECT MIN(EntryTable.time_minutes) AS minTime, MAX(EntryTable.time_minutes) AS maxTime " +
            "FROM EntryTable " +
            "INNER JOIN ExtraDataTable ON EntryTable.id = ExtraDataTable.entry_id " +
            "WHERE EntryTable.date BETWEEN :startDate AND :endDate " +
            "AND ExtraDataTable.reminder_type = :specificReminder ")
    suspend fun findEarliestEventsInRangeWithReminder(
        startDate: String,
        endDate: String,
        specificReminder: String
    ): MinMaxTimeResult


    // Find all entries whose title contains a specified text fragment
    // and whose event time is later than a specified time.
    @Query("SELECT * FROM EntryTable " +
            "WHERE entry LIKE '%' || :textFragment || '%' " +
            "AND time_minutes >= :timeFloor")
    suspend fun findEntriesContainsSpecificTextTimeIsLaterThanSpecifiedTime(
        textFragment: String,
        timeFloor: Int
    ): List<EntryTable>




    // (DROPPED) Find Top X Dates With Most Entries
    // (DROPPED) group by issue
    /*
    @Query("SELECT date, COUNT(*) as entryCount FROM EntryTable " +
            "GROUP BY date " +
            "ORDER BY entryCount DESC " +
            "LIMIT :limit")
    suspend fun findTopDatesWithMostEntries(
        limit: Int
    )


    // (DROPPED) Find all reminder types, count them and show those that are more than / equal to "amount"
    // (DROPPED) group by / having issue
    @Query("SELECT ExtraDataTable.reminder_type, COUNT(reminder_type) reminderCount FROM EntryTable " +
            "INNER JOIN ExtraDataTable ON  EntryTable.id = ExtraDataTable.entry_id " +
            "GROUP BY ExtraDataTable.reminder_type " +
            "HAVING COUNT(reminder_type) >= :amount " +
            "ORDER BY ExtraDataTable.reminder_type ")
    suspend fun reminderTypeCountMoreThanEqualAmount(
        amount: Int
    ): List<EntryTable>
     */




}