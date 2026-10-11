package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.helper_classes.CouchbaseLite

import com.couchbase.lite.MutableDocument

data class CBL_EntryWithExtraData(
    val entry: MutableDocument,
    val extraData: MutableDocument?
)