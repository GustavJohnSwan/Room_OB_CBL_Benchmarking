package com.bignerdranch.android.room_ob_cbl_benchmarking.database


import androidx.room.Embedded

data class Room_EntryWithExtraData(
    @Embedded
    val entry: EntryTable,

    @Embedded
    val extraData: ExtraDataTable?
)