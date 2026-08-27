package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.data_mapping

import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.GeneratedEvent
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryOb_B
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.ExtraDataOb_B
import io.objectbox.BoxStore

class OB_Mapping (store: BoxStore) {

    val EOBBox = store.boxFor(EntryOb_B::class.java)
    val EDOBBox = store.boxFor(ExtraDataOb_B::class.java)

    fun map(listOfDataObjects: List<GeneratedEvent>): List<EntryOb_B> {


        val listOfEntityDataObjects = mutableListOf<EntryOb_B>()

        listOfDataObjects.forEach {

            val entryEntity = EntryOb_B(
                dateOb = it.date,
                entryOb = it.title,
                timeMinutesOb = it.time
            )

            if (it.extraData != null) {
                val extraEntity = ExtraDataOb_B(
                    reminderTypeOb = it.extraData.reminderType,
                    repeatOb = it.extraData.repeatType,
                    repeatDetailsOb = it.extraData.repeatDetails
                )

                entryEntity.extradataob_b.target = extraEntity

            }

            listOfEntityDataObjects.add(entryEntity)
        }

        return listOfEntityDataObjects

    }
}