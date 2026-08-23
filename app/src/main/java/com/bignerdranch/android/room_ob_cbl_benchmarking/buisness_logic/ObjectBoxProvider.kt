package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic

import android.content.Context

import com.bignerdranch.android.room_ob_cbl_benchmarking.database.MyObjectBox
import io.objectbox.BoxStore

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
}