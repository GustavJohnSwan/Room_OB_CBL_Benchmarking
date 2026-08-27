package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.DAO


import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryAttachmentOb_B
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryOb_B
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryOb_B_
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.ExtraDataOb_B
import io.objectbox.BoxStore

class OB_DAO (store: BoxStore) {

    // creating boxes for each database object
    val EOBBox = store.boxFor(EntryOb_B::class.java)
    val EDOBBox = store.boxFor(ExtraDataOb_B::class.java)
    val EAOBBox = store.boxFor(EntryAttachmentOb_B::class.java)


    // _____________________________________________________________________________________________
    // 27.08.26 DAO

    fun insertEntriesBulk(entries: List<EntryOb_B>) {
        EOBBox.put(entries)
    }

    fun deleteAllEntries() {
        EOBBox.removeAll()
    }


    // benchmarking DAO functions

    // INSERT BULK (EntryOb only)
    /*
    fun insertEntriesBulk(entries: List<EntryOb_B>) {
        EOBBox.put(entries)
    }

     */

    // GET BULK (EntryOb only)
    fun getAllEntriesBulk(): List<EntryOb_B> {
        return EOBBox.all
    }

    //----------------------------------------------------------------------------------------------





    //----------------------------------------------------------------------------------------------
    // first draft for basic DAO functions
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