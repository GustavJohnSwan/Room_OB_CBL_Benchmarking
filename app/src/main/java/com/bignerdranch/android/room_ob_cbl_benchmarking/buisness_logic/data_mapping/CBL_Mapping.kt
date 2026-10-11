package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.data_mapping

import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.helper_classes.CouchbaseLite.CBL_EntryWithExtraData
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.GeneratedEvent
import com.couchbase.lite.MutableDocument

class CBL_Mapping {

    fun map(
        events: List<GeneratedEvent>,
        firstId: Long
    ): List<CBL_EntryWithExtraData> {
        return events.mapIndexed { index, event ->
            val entry = mapMainEntry(event, firstId + index)

            val extraData = event.extraData?.let { extra ->
                MutableDocument()
                    .setString("entry_id", entry.id)
                    .setString("reminder_type", extra.reminderType)
                    .setString("repeat", extra.repeatType)
                    .setString("repeat_details", extra.repeatDetails)
            }

            CBL_EntryWithExtraData(entry, extraData)
        }
    }

    fun mapMainEntries(
        events: List<GeneratedEvent>,
        firstId: Long
    ): List<MutableDocument> {
        return events.mapIndexed { index, event ->
            mapMainEntry(event, firstId + index)
        }
    }

    private fun mapMainEntry(
        event: GeneratedEvent,
        id: Long
    ): MutableDocument {
        return MutableDocument(id.toString())
            .setString("date", event.date)
            .setString("entry", event.title)
            .setInt("time_minutes", event.time)
    }
}