package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic

import android.content.Context

import com.bignerdranch.android.room_ob_cbl_benchmarking.database.MyObjectBox
import io.objectbox.BoxStore
import io.objectbox.BoxStoreBuilder
import java.io.File

object ObjectBoxProvider {

    @Volatile
    private var store: BoxStore? = null

    fun init(context: Context) {
        if (store == null) {
            synchronized(this) {
                if (store == null) {
                    store = MyObjectBox.builder()
                        .androidContext(context.applicationContext)
                        .build()
                }
            }
        }
    }

    fun get(): BoxStore =
        store ?: error("ObjectBoxProvider not initialized")



    // Close "store" box and assign null, delete Objectbox database (all files and data) (this also resets the ID counter). Build a new database
    fun reset(context: Context) {

        store?.close()

        store = null

        BoxStore.deleteAllFiles(context.applicationContext, null)

        store = MyObjectBox.builder()
            .androidContext(context.applicationContext)
            .build()

    }

}