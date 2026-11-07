package com.bignerdranch.android.room_ob_cbl_benchmarking

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SqliteLimitTest {

    @Test
    fun checkParameterLimit() {
        val db = SQLiteDatabase.create(null)

        try {
            for (amount in listOf(999, 1000, 32766, 32767)) {
                val placeholders = List(amount) { "?" }.joinToString(",")
                val sql = "SELECT 1 WHERE '1' IN ($placeholders)"
                val arguments = Array(amount) { "1" }

                try {
                    db.rawQuery(sql, arguments).use { cursor ->
                        check(cursor.moveToFirst())
                    }
                    Log.d("SQLITE_LIMIT", "$amount parameters: OK")
                } catch (e: SQLiteException) {
                    Log.d("SQLITE_LIMIT", "$amount parameters: FAILED — ${e.message}")
                }
            }
        } finally {
            db.close()
        }
    }
}