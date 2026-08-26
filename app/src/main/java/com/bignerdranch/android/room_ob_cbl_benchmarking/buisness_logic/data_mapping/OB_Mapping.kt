package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.data_mapping

import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.GeneratedEvent
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryOb_B
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.ExtraDataOb_B
import io.objectbox.BoxStore

class OB_Mapping (store: BoxStore) {

    val EOBBox = store.boxFor(EntryOb_B::class.java)
    val EDOBBox = store.boxFor(ExtraDataOb_B::class.java)

    fun map(listOfDataObjects: List<GeneratedEvent>) {



        listOfDataObjects.forEach {

            // TEST IF THIS WORKES

            val entryEntity = EntryOb_B(
                dateOb = it.date,
                entryOb = it.title,
                timeMinutesOb = it.time
            )

            EOBBox.put(entryEntity)
        }

    }
}