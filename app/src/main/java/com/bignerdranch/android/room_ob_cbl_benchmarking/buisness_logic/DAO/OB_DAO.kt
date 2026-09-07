package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.DAO


import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.GeneratedEvent
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryAttachmentOb_B
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryOb_B
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryOb_B_
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.ExtraDataOb_B
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.ExtraDataOb_B_
import io.objectbox.BoxStore
import io.objectbox.query.QueryBuilder

class OB_DAO (private val store: BoxStore) {

    // creating boxes for each database object
    val EOBBox = store.boxFor(EntryOb_B::class.java)
    val EDOBBox = store.boxFor(ExtraDataOb_B::class.java)
    val EAOBBox = store.boxFor(EntryAttachmentOb_B::class.java)




    // _____________________________________________________________________________________________
    // Basic CRUD

    // INSERT BULK
    // also UPDATE BULK - if entries with the same ID already exist
    // Function for JSON data set
    // Data set insertion - inserts the provided data set from provided JSON file (100 - 100 000 entries)
    // this function inserts main data entry AND if it has extra data associated with it, then that as well.
    // No need for seperate EDOBBox.put()

    // OUTDATED - for objectbox DAO, put() can be used for both inserting and updating entries.
    // But if used that way, the function needs to be defined in a more universal way, using both EOBBox and EDOBBox
/*
    fun insertEntriesBulk(entries: List<EntryOb_B>): List<Long> {
        EOBBox.put(entries)

        return entries.map { it.id }
    }

        // INSERT ENTRY - main data AND extra data if it exists.
    // also UPDATE ENTRY - if entry with same ID already exists
    // Extra data needs to be associated with main data using "entry.extradataob_b.target = extraEntity"
    fun insertEntryOb_B(entry: EntryOb_B): Long {
        return EOBBox.put(entry)
    }

 */


    fun putEntry(
        entry: EntryOb_B,
        extraDataIdToDelete: Long? = null
    ): Long {

        val existingExtraData = entry.extradataob_b.target

        store.runInTx {

            // Existing ExtraData that was modified
            if (existingExtraData != null && existingExtraData.id != 0L) {
                EDOBBox.put(existingExtraData)
            }

            // Put/update main Entry.
            // New ExtraData with id=0 can be persisted through the ToOne relation.
            EOBBox.put(entry)

            // Remove old ExtraData if this was ExtraData -> null
            if (extraDataIdToDelete != null && extraDataIdToDelete != 0L) {
                EDOBBox.remove(extraDataIdToDelete)
            }
        }

        return entry.id
    }

    fun putEntries(
        entries: List<EntryOb_B>,
        extraDataIdsToDelete: List<Long> = emptyList()
    ): List<Long> {

        val existingExtraData = entries
            .mapNotNull { it.extradataob_b.target }
            .filter { it.id != 0L }

        store.runInTx {

            if (existingExtraData.isNotEmpty()) {
                EDOBBox.put(existingExtraData)
            }

            EOBBox.put(entries)

            if (extraDataIdsToDelete.isNotEmpty()) {
                EDOBBox.removeByIds(extraDataIdsToDelete)
            }
        }

        return entries.map { it.id }
    }



    // COUNT all entries
    fun countEntries(): Long {
        return EOBBox.count()
    }



    // GET BULK (EntryOb only, but ExtraDataOb_B can be and is accessed using it.extradataob_b.target)
    fun getAllEntriesBulk(): List<EntryOb_B> {
        return EOBBox.all
    }


    // GET ENTRY based on ID
    fun getSpecificEntryOb_B(id: Long): EntryOb_B? {

        val entry = EOBBox.get(id)

        entry?.extradataob_b?.target

        return entry
    }

    fun getEntriesByIDs(entryIds: List<Long>): List<EntryOb_B> {

        val entries = EOBBox.get(entryIds)

        entries.forEach { entry ->
            entry.extradataob_b.target
        }

        return entries
    }


    // DELETE ENTRY based on ID (both its mother and child object : EntryOb_B and its ExtraDataOb_B if it exists)
    fun deleteEntry(id: Long) {
        val entry = EOBBox.get(id) ?: return
        val extraDataId = entry.extradataob_b.targetId

        store.runInTx {
            EOBBox.remove(id)

            if (extraDataId != 0L) {
                EDOBBox.remove(extraDataId)
            }
        }
    }


    // DELETE BULK based on ID (both its mother and child object : EntryOb_B and its ExtraDataOb_B if it exists)
    fun deleteEntries(entryIds: List<Long>) {

        val entries = EOBBox.get(entryIds)

        val extraDataIds = entries
            .map { it.extradataob_b.targetId }
            .filter { it != 0L }

        store.runInTx {
            EOBBox.removeByIds(entryIds)
            EDOBBox.removeByIds(extraDataIds)
        }
    }




    // DELETE ALL ObjectBox database entries
    fun deleteAllEntries() {
        store.runInTx {
            EOBBox.removeAll()
            EDOBBox.removeAll()
        }
    }



    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // Medium Queries

    // Find entries in date, order by time in ObjectBox database
    fun findEntriesInSpecificDate(date: String): List<EntryOb_B> {
        val query = EOBBox.query(EntryOb_B_.dateOb.equal(date)).order(EntryOb_B_.timeMinutesOb).build()
        val desiredEntries = query.find()
        query.close()

        return desiredEntries
    }

    // Find entries in date range, order by time in ObjectBox database
    fun findEntriesInDateRange(startDate: String, endDate: String): List<EntryOb_B> {
        val query = EOBBox.query(
            EntryOb_B_.dateOb.greaterOrEqual(startDate, QueryBuilder.StringOrder.CASE_SENSITIVE)
                .and
                    (
                    EntryOb_B_.dateOb.lessOrEqual(endDate, QueryBuilder.StringOrder.CASE_SENSITIVE)
                            )
        )
            .order(EntryOb_B_.timeMinutesOb)
            .build()
        val desiredEntries = query.find()
        query.close()

        return desiredEntries
    }


    // Find next X entries from today to date with reminder (not null) in ObjectBox database
    fun findNextEntry(startDate: String, endDate: String, amount: Long): List<EntryOb_B> {
        val queryBuilder = EOBBox.query(
            EntryOb_B_.dateOb.greaterOrEqual(startDate, QueryBuilder.StringOrder.CASE_SENSITIVE)
                .and
                    (
                    EntryOb_B_.dateOb.lessOrEqual(endDate, QueryBuilder.StringOrder.CASE_SENSITIVE)
                )
        )

        queryBuilder.link(EntryOb_B_.extradataob_b).apply(ExtraDataOb_B_.reminderTypeOb.notNull())


            val query = queryBuilder
            .order(EntryOb_B_.dateOb)
            .order(EntryOb_B_.timeMinutesOb)
            .build()

        val desiredEntries = query.find(0, amount)
        query.close()

        return desiredEntries
    }

    // Find entries with a specific reminder
    fun findEntriesWithSpecificReminder(): List<EntryOb_B> {

        val queryBuilder = EOBBox.query()

        queryBuilder
            .link(EntryOb_B_.extradataob_b)
            .apply(
                ExtraDataOb_B_.reminderTypeOb.equal("10 mins before")
            )
        val query = queryBuilder.build()


        val desiredEntries = query.find()
        query.close()

        return desiredEntries
    }


    // Find entries with any recurrence
    fun findEntriesWithRecurrance(): List<EntryOb_B> {

        val queryBuilder = EOBBox.query()

        queryBuilder
            .link(EntryOb_B_.extradataob_b)
            .apply(
                ExtraDataOb_B_.repeatOb.notNull()
            )
        val query = queryBuilder.build()


        val desiredEntries = query.find()
        query.close()


        return desiredEntries

    }



    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________

    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // ADVANCED QUERIES

    // UPDATE entries in date range, in ObjectBox database
    fun findEntriesInDateRangeForUpdate(
        startDate: String,
        endDate: String
    ): List<EntryOb_B> {

        val query = EOBBox.query(
            EntryOb_B_.dateOb
                .greaterOrEqual(
                    startDate,
                    QueryBuilder.StringOrder.CASE_SENSITIVE
                )
                .and(
                    EntryOb_B_.dateOb.lessOrEqual(
                        endDate,
                        QueryBuilder.StringOrder.CASE_SENSITIVE
                    )
                )
        )
            .order(EntryOb_B_.id)
            .build()

        val desiredEntries = query.find()

        query.close()

        return desiredEntries
    }


    // DELETE entries in date range, in ObjectBox database
    fun findEntriesInDateRangeForDelete(
        startDate: String,
        endDate: String
    ){

        val query = EOBBox.query(
            EntryOb_B_.dateOb
                .greaterOrEqual(
                    startDate,
                    QueryBuilder.StringOrder.CASE_SENSITIVE
                )
                .and(
                    EntryOb_B_.dateOb.lessOrEqual(
                        endDate,
                        QueryBuilder.StringOrder.CASE_SENSITIVE
                    )
                )
        ).build()

        val entries = query.find()

        val entryIds = entries
            .map { it.id }

        val extraDataIds = entries
            .map { it.extradataob_b.targetId }
            .filter { it != 0L }

        store.runInTx {
            EOBBox.removeByIds(entryIds)
            EDOBBox.removeByIds(extraDataIds)
        }

        query.close()

    }





    // _____________________________________________________________________________________________
    // benchmarking DAO functions

    // INSERT BULK (EntryOb only)
    /*
    fun insertEntriesBulk(entries: List<EntryOb_B>) {
        EOBBox.put(entries)
    }
     */

    // GET BULK (EntryOb only)
    /*
fun getAllEntriesBulk(): List<EntryOb_B> {

    val entries = EOBBox.all

    entries.forEach { entry ->
        entry.extradataob_b.target
    }

    return entries
}
     */

    //----------------------------------------------------------------------------------------------





    //----------------------------------------------------------------------------------------------
    // first draft for basic DAO functions
    /*
    fun insertEntryOb_B(entry: EntryOb_B): Long {
        EOBBox.put(entry)
        return entry.id
    }

    fun insertExtraDataOb_B(entry: ExtraDataOb_B): Long {
        EDOBBox.put(entry)
        return entry.id
    }



    fun getAllEntryOb_Bs(): List<EntryOb_B> {
        return EOBBox.all
    }


    fun getAllExtraDataOb_Bs(): List<ExtraDataOb_B> {
        return EDOBBox.all
    }




    // id is an argument for calling this function from buisness logic
    fun getSpecificEntryOb_B(id: Long): EntryOb_B? {
        return EOBBox.get(id)
    }



    // EntryOb_B_ is the metadata created object code that is generated when building a project with objectbox database. You use EntryOb_B to edit the actual object data,
    // you use EntryOb_B_ to query / read already existing data
    fun getEntryOb_BsType1(): List<EntryOb_B> {
        val query = EOBBox
            .query(EntryOb_B_.entryOb.startsWith ("G"))
            .order(EntryOb_B_.entryOb)
            .build()
        val results = query.find()
        query.close()
        return results
    }


    fun removeEntryOb_B(id: Long): String {
        EOBBox.remove(id)
        return "EntryOb_B $id removed"
    }



    fun removeEntryOb_BAll(): String {
        EOBBox.removeAll()
        return "All EntryOb_Bs removed"
    }

    fun countExtraDataOb_Bs(): Long {
        return EDOBBox.count()
    }

     */


/*
Example of deleting a parent object and its child done correctly :
- retrieve entry
- retrieve extradata entry ID from retrieved entry
- define both deletion actions under one transaction .runInTx (this is safer in case of crash)
- perform deletions

fun deleteCompleteEntry(entryId: Long) {
    val entry = entryBox.get(entryId) ?: return
    val extraDataId = entry.extradataob_b.targetId

    boxStore.runInTx {
        entryBox.remove(entryId)

        if (extraDataId != 0L) {
            extraDataBox.remove(extraDataId)
        }
    }
}
 */






}