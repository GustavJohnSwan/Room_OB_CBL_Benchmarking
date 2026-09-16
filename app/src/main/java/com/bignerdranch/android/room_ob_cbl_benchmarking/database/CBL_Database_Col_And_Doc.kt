package com.bignerdranch.android.room_ob_cbl_benchmarking.database

import com.bignerdranch.android.room_ob_cbl_benchmarking.database.database_setup.CouchbaseLiteProvider
import com.couchbase.lite.Collection




class CBL_Database_Col_And_Doc {


    // Create "entries" collection
    val entriesCollection: Collection = CouchbaseLiteProvider
        .getDatabase()
        .createCollection("entries")


    // Create "extra_data" collection
    val extraDataCollection: Collection = CouchbaseLiteProvider
        .getDatabase()
        .createCollection("extra_data")







}

