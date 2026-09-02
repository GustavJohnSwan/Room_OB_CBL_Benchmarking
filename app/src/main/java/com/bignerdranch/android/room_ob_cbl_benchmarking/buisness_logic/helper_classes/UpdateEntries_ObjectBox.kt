package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.helper_classes

import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.GeneratedEvent
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.GeneratedExtraData
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryOb_B
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.ExtraDataOb_B

class UpdateEntries_ObjectBox() {
    fun update(listOfEntityDataObjects: List<EntryOb_B>, listOfDataObjects: List<GeneratedEvent>): List<EntryOb_B> {

        listOfEntityDataObjects.zip(listOfDataObjects).forEach { (EntityObject, GeneratedObject) ->

            EntityObject.dateOb = GeneratedObject.date
            EntityObject.entryOb = GeneratedObject.title
            EntityObject.timeMinutesOb = GeneratedObject.time

            var EntityExtraData = EntityObject.extradataob_b.target
            var GeneratedExtraData = GeneratedObject.extraData

            if (EntityExtraData != null && GeneratedExtraData != null) {
                EntityExtraData.reminderTypeOb = GeneratedExtraData.reminderType
                EntityExtraData.repeatOb = GeneratedExtraData.repeatType
                EntityExtraData.repeatDetailsOb = GeneratedExtraData.repeatDetails
            }
        }

        return listOfEntityDataObjects

    }
}