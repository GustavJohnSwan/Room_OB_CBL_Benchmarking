package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.helper_classes.Room

import androidx.room.Embedded
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryTable
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.ExtraDataTable

data class Room_EntryWithExtraData(
    @Embedded
    val entry: EntryTable,

    @Embedded
    val extraData: ExtraDataTable?
)