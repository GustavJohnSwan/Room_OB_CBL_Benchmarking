package com.bignerdranch.android.room_ob_cbl_benchmarking.database.database_setup

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.couchbase.lite.CouchbaseLite
import com.couchbase.lite.DataSource.collection
import com.couchbase.lite.Database
import com.couchbase.lite.Replicator


object CouchbaseLiteProvider {
    private var database: Database? = null

    // One-off initialization
    fun init(context: Context) {
        CouchbaseLite.init(context.applicationContext)
        Log.i(TAG, "CBL Initialized")


        // Create a database
        if (database == null) {
            database = Database("benchmark_database")
        }



    }

    fun getDatabase(): Database {
        return database
            ?: error("CouchbaseLiteProvider not initialized")
    }



}
