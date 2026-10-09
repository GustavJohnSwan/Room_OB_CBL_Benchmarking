package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.data_mapping

import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.helper_classes.Room.Room_EntryWithExtraData
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.GeneratedEvent
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryTable
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.ExtraDataTable

class Room_Mapping {

    fun map(events: List<GeneratedEvent>): List<Room_EntryWithExtraData> {
        return events.map { event ->
            Room_EntryWithExtraData(
                entry = EntryTable(
                    dateDB = event.date,
                    entryDB = event.title,
                    timeMinutes = event.time
                ),
                extraData = event.extraData?.let { extra ->
                    ExtraDataTable(
                        entryId = 0L,
                        reminderType = extra.reminderType,
                        repeat = extra.repeatType,
                        repeatDetails = extra.repeatDetails
                    )
                }
            )
        }
    }

    fun mapMainEntries(events: List<GeneratedEvent>): List<EntryTable> {
        return events.map { event ->
            EntryTable(
                dateDB = event.date,
                entryDB = event.title,
                timeMinutes = event.time
            )
        }
    }
}