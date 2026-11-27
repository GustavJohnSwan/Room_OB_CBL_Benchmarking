package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic

import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryTable

data class PreparedBenchmarkData_4(
    val name: String
) {
    val amount: MutableList<Int> = mutableListOf()

    val entries: MutableList<MutableList<List<EntryTable>>> =
        mutableListOf()

    val entriesToDelete: MutableList<MutableList<List<EntryTable>>> =
        mutableListOf()

    val mainEntries: MutableList<List<EntryTable>> =
        mutableListOf()
}

// Data that needs to be pre-generated once and stored in this data object for Q19 and Q22
/*

Q19
var amount = roomViewModel.firstXamountIDs_Room(j)
var entries = roomViewModel.updateEntriesBenchmark_Room(amount)

Q22
val entriesToDelete = roomViewModel.deleteEntriesByIdsBenchmark_Room(j)

 */