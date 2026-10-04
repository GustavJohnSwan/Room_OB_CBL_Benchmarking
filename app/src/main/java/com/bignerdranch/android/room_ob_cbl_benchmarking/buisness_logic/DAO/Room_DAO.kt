package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.helper_classes.Room.MinMaxTimeResult
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.helper_classes.Room.RepeatTypeCountResult
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryTable
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.Room_EntryWithExtraData
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.ExtraDataTable

@Dao
interface Room_DAO {

    // currently just copied 3 functions from calendar app. Need to test basic insert, query and delete
    /*
    @Insert
    suspend fun insert_IntoEntryTable(entryTable: EntryTable): Long

    @Query("SELECT * FROM EntryTable")
    suspend fun get_AllEntries(): List<EntryTable>

    @Delete
    suspend fun delete_Entry(entry: EntryTable)
     */


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // Room CRUD

    // *** CRUD - INSERT main ENTRY data entry
    @Insert
    suspend fun insertMainEntry(entryTable: EntryTable): Long

    // INSERT extra ENTRY data entries
    @Insert
    suspend fun insertExtraData(extraData: ExtraDataTable)

    // INSERT FULL ENTRY
    @Transaction
    suspend fun insertEntry(value: Room_EntryWithExtraData): Long {
        val entryId = insertMainEntry(value.entry)

        val extraData = value.extraData
        if (extraData != null) {
            insertExtraData(
                extraData.copy(
                    entryId = entryId
                )
            )
        }

        return entryId
    }

    // *** CRUD - INSERT BULK main data entries
    @Insert
    suspend fun insertMainEntries(entries: List<EntryTable>): List<Long>

    // INSERT BULK extra data entries
    @Insert
    suspend fun insertExtraDataEntries(entries: List<ExtraDataTable>)

    // *** DATA SET INSERT - INSERT FULL ENTRIES
    @Transaction
    suspend fun insertEntries(
        entries: List<Room_EntryWithExtraData>
    ): List<Long> {
        val entryIds = insertMainEntries(entries.map { it.entry })

        val extraDataEntries = entries.mapIndexedNotNull { index, value ->
            value.extraData?.copy(entryId = entryIds[index])
        }

        if (extraDataEntries.isNotEmpty()) {
            insertExtraDataEntries(extraDataEntries)
        }

        return entryIds
    }

    // *** CRUD - UPDATE ENTRY if it exists
    @Update
    suspend fun updateEntry(entry: EntryTable)

    // *** CRUD - UPDATE ENTRIES if it exists
    @Update
    suspend fun updateEntries(entries: List<EntryTable>)

    // DO NOT BENCHMARK UPSERT - IT REQUIRES A MORE COMPLEX AND SEPARATE DATA SET SAMPLE THAT YOU DON'T HAVE
    // YOU CAN'T USE THE SAME DATA SET SAMPLE YOU USED FOR UPDATE
    // UPDATE ENTRY if it exists, otherwise INSERT IT
    @Upsert
    suspend fun insertOrUpdateEntry(entry: EntryTable): Long

    // UPDATE ENTRIES if it exists, otherwise INSERT THEM
    @Upsert
    suspend fun insertOrUpdateEntries(entries: List<EntryTable>): List<Long>



    // *** CRUD - COUNT all entries
    @Query("SELECT COUNT(id) FROM EntryTable")
    suspend fun countEntries(): Long



    // *** CRUD - GET BULK ALL ENTRIES
    @Query("SELECT e.*, x.* " +
            "FROM EntryTable AS e " +
            "LEFT JOIN ExtraDataTable AS x " +
            "ON e.id = x.entry_id ")
    suspend fun getAllEntriesBulk(): List<Room_EntryWithExtraData>

    // *** CRUD - GET ENTRY BY ID
    @Query("SELECT e.*, x.* " +
            "FROM EntryTable AS e " +
            "LEFT JOIN ExtraDataTable AS x " +
            "ON e.id = x.entry_id " +
            "WHERE id = :entryId")
    suspend fun getSpecificEntry(entryId: Long): Room_EntryWithExtraData?

    // *** CRUD - GET ENTRIES BY IDs
    @Query("SELECT e.*, x.* " +
            "FROM EntryTable AS e " +
            "LEFT JOIN ExtraDataTable AS x " +
            "ON e.id = x.entry_id " +
            "WHERE id IN (:entryId)")
    suspend fun getEntriesByIDs(entryId: List<Long>): List<Room_EntryWithExtraData>



    // *** CRUD - DELETE ENTRY
    @Delete
    suspend fun deleteEntry(entry: EntryTable): Int

    // *** CRUD - DELETE ENTRIES
    @Delete
    suspend fun deleteEntries(entries: List<EntryTable>): Int


    // *** CRUD - DELETE ALL ENTRIES
    @Query("DELETE FROM EntryTable")
    suspend fun deleteAllEntries()


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // Medium Queries

    // *** MQ - Find entries in date, order by time in Room database
    @Query("SELECT e.*, x.* " +
            "FROM EntryTable AS e " +
            "LEFT JOIN ExtraDataTable AS x " +
            "ON e.id = x.entry_id " +
            "WHERE date = :entryDate " +
            "ORDER BY time_minutes ASC")
    suspend fun findEntriesInSpecificDate(entryDate: String): List<Room_EntryWithExtraData>

    // Find entries in date range, order by time in Room database
    @Query("SELECT e.*, x.* " +
            "FROM EntryTable AS e " +
            "LEFT JOIN ExtraDataTable AS x " +
            "ON e.id = x.entry_id " +
            "WHERE date BETWEEN :startDate AND :endDate " +
            "ORDER BY date ASC, " +
            "time_minutes ASC")
    suspend fun findEntriesInDateRange(startDate: String, endDate: String): List<Room_EntryWithExtraData>

    // Find next X entries from date to date with reminder (not null) in Room database
    @Query("SELECT e.*, x.* " +
            "FROM EntryTable AS e " +
            "INNER JOIN ExtraDataTable AS x " +
            "ON e.id = x.entry_id " +
            "WHERE e.date BETWEEN :startDate AND :endDate " +
            "AND x.reminder_type IS NOT NULL " +
            "ORDER BY e.date ASC, e.time_minutes ASC, e.id ASC " +
            "LIMIT :nextAmount")
    suspend fun findNextEntries(startDate: String, endDate: String, nextAmount: Int): List<Room_EntryWithExtraData>

    // Find entries with a specific reminder
    @Query("SELECT e.*, x.* " +
            "FROM EntryTable AS e " +
            "INNER JOIN ExtraDataTable AS x " +
            "ON e.id = x.entry_id " +
            "WHERE x.reminder_type = :desiredReminderType")
    suspend fun findEntriesWithSpecificReminder(desiredReminderType: String): List<Room_EntryWithExtraData>

    // Find entries with any recurrence
    @Query("SELECT e.*, x.* " +
            "FROM EntryTable AS e " +
            "INNER JOIN ExtraDataTable AS x " +
            "ON e.id = x.entry_id " +
            "WHERE x.repeat IS NOT NULL")
    suspend fun findEntriesWithRecurrence(): List<Room_EntryWithExtraData>


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // Advanced Queries


    // FIND Entries in Date Range with specific Reminder and specific Repeat1 or Repeat2

    @Query("SELECT e.*, x.* " +
            "FROM EntryTable AS e " +
            "INNER JOIN ExtraDataTable AS x " +
            "ON e.id = x.entry_id " +
            "WHERE e.date BETWEEN :startDate AND :endDate " +
            "AND x.reminder_type = :specificReminder " +
            "AND (" +
            "x.repeat = :specificRepeat1 " +
            "OR x.repeat = :specificRepeat2 " +
            ") " +
            "ORDER BY e.date, e.time_minutes ASC, e.id ASC " +
            "LIMIT :amount ")
    suspend fun findEntriesInDateRangeReminderRepeat1OrRepeat2(
        startDate: String,
        endDate: String,
        specificReminder: String,
        specificRepeat1: String,
        specificRepeat2: String,
        amount: Int
        ): List<Room_EntryWithExtraData>


    // FIND Entries with Reminder is Null and Repeat is Not Null + Limit + Offset
    @Query("SELECT e.*, x.* " +
            "FROM EntryTable AS e " +
            "INNER JOIN ExtraDataTable AS x ON e.id = x.entry_id " +
            "WHERE x.reminder_type IS NULL " +
            "AND x.repeat IS NOT NULL " +
            "ORDER BY e.date ASC, " +
            "e.time_minutes ASC, " +
            "e.id ASC " +
            "LIMIT :limit " +
            "OFFSET :offset")
    suspend fun findEntriesReminderNullRepeatNotNullLimitOffset(
        limit: Int,
        offset: Int
    ): List<Room_EntryWithExtraData>


    // Find all repeat types and count them
    @Query("SELECT ExtraDataTable.repeat AS repeatType, COUNT(repeat) AS repeatCount " +
            "FROM ExtraDataTable " +
            "WHERE ExtraDataTable.repeat IS NOT NULL " +
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
    @Query("SELECT e.*, x.* FROM EntryTable AS e " +
            "LEFT JOIN ExtraDataTable AS x ON e.id = x.entry_id " +
            "WHERE instr(entry, :textFragment) > 0 " +
            "AND time_minutes >= :timeFloor")
    suspend fun findEntriesContainsSpecificTextTimeIsLaterThanSpecifiedTime(
        textFragment: String,
        timeFloor: Int
    ): List<Room_EntryWithExtraData>




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