package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.data_mapping

import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.GeneratedEvent
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.database_setup.CouchbaseLiteProvider
import com.couchbase.lite.MutableDocument

class CBL_UpdateMapping {

    private val entries = CouchbaseLiteProvider.getDatabase().getCollection("entries")

    fun map(
        id: String,
        event: GeneratedEvent
    ): MutableDocument {
        val document = checkNotNull(entries?.getDocument(id)) {
            "Document $id not found"
        }.toMutable()

        document.setString("date", event.date)
        document.setString("entry", event.title)
        document.setInt("time_minutes", event.time)

        return document
    }


    fun map(
        ids: List<String>,
        events: List<GeneratedEvent>
    ): List<MutableDocument> {
        require(ids.size == events.size)

        return ids.indices.map { index ->
            map(ids[index], events[index])
        }
    }

}