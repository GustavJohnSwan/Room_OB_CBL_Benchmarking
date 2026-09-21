package com.bignerdranch.android.room_ob_cbl_benchmarking.database.database_setup

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.createCollections
import com.couchbase.lite.CouchbaseLite

import com.couchbase.lite.Database



object CouchbaseLiteProvider {
    private var database: Database? = null

    // One-off initialization
    fun init(context: Context) {
        CouchbaseLite.init(context.applicationContext)
        Log.i(TAG, "CBL Initialized")


        // Create a database
        if (database == null) {
            database = Database("benchmark_database")

            // create collections
            createCollections()
        }



    }

    fun getDatabase(): Database {
        return database
            ?: error("CouchbaseLiteProvider not initialized")
    }



}
