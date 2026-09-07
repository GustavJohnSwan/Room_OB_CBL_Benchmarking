package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.helper_classes

import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.GeneratedEvent
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.GeneratedExtraData
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryOb_B
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.ExtraDataOb_B

class UpdateEntries_ObjectBox {

    fun update(
        listOfEntityDataObjects: List<EntryOb_B>,
        listOfDataObjects: List<GeneratedEvent>
    ): Pair<List<EntryOb_B>, List<Long>> {

        val extraDataIdsToDelete = mutableListOf<Long>()

        listOfEntityDataObjects.zip(listOfDataObjects)
            .forEach { (EntityObject, GeneratedObject) ->

                EntityObject.dateOb = GeneratedObject.date
                EntityObject.entryOb = GeneratedObject.title
                EntityObject.timeMinutesOb = GeneratedObject.time

                val EntityExtraData = EntityObject.extradataob_b.target
                val GeneratedExtraData = GeneratedObject.extraData

                when {

                    // ExtraData -> ExtraData
                    EntityExtraData != null && GeneratedExtraData != null -> {

                        EntityExtraData.reminderTypeOb =
                            GeneratedExtraData.reminderType

                        EntityExtraData.repeatOb =
                            GeneratedExtraData.repeatType

                        EntityExtraData.repeatDetailsOb =
                            GeneratedExtraData.repeatDetails
                    }

                    // null -> ExtraData
                    EntityExtraData == null && GeneratedExtraData != null -> {

                        val newExtraData = ExtraDataOb_B(
                            reminderTypeOb = GeneratedExtraData.reminderType,
                            repeatOb = GeneratedExtraData.repeatType,
                            repeatDetailsOb = GeneratedExtraData.repeatDetails
                        )

                        EntityObject.extradataob_b.target = newExtraData
                    }

                    // ExtraData -> null
                    EntityExtraData != null && GeneratedExtraData == null -> {

                        extraDataIdsToDelete.add(EntityExtraData.id)

                        EntityObject.extradataob_b.target = null
                    }

                    // null -> null
                    else -> {
                        // Nothing
                    }
                }
            }

        return Pair(
            listOfEntityDataObjects,
            extraDataIdsToDelete
        )
    }
}