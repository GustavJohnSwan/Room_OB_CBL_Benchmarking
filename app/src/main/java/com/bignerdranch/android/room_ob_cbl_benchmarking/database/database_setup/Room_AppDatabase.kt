package com.bignerdranch.android.room_ob_cbl_benchmarking.database.database_setup

import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.DAO.Room_DAO
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryAttachment
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryTable
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.ExtraDataTable

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


// this class sets up the database - defines its configuration and acts as the main access point to the data
// version needs to be incremented every time the schema is updated/changed
@Database(entities = [EntryTable::class, ExtraDataTable::class, EntryAttachment::class], version = 17)
abstract class AppDatabase : RoomDatabase() { // AppDatabase extends RoomDatabase
    // returns DAO interfaces
    abstract fun Room_DAO(): Room_DAO





    // this creates a shared, static variable called INSTANCE. It holds the only copy of the database. It is a SINGLETON that allows only one INSTANCE to exist in the whole app
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // this returns an existing database instance or it creates a new one
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) { // this makes sure that only one thread initializes the database
                val instance = Room.databaseBuilder(
                    context.applicationContext, // this prevents memory leaks by not tying the database to an Activity context
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // this makes ROOM detect if there has been a version change. It wipes and rebuild the DB (deletes data)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}