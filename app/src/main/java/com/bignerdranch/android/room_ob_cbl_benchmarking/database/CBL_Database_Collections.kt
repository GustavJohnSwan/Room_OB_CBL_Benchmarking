package com.bignerdranch.android.room_ob_cbl_benchmarking.database

import com.bignerdranch.android.room_ob_cbl_benchmarking.database.database_setup.CouchbaseLiteProvider
import com.couchbase.lite.Collection

fun createCollections() {

    // Create "entries" collection
    val entriesCollection: com.couchbase.lite.Collection = CouchbaseLiteProvider
        .getDatabase()
        .createCollection("entries")


    // Create "extra_data" collection
    val extraDataCollection: Collection = CouchbaseLiteProvider
        .getDatabase()
        .createCollection("extra_data")

}