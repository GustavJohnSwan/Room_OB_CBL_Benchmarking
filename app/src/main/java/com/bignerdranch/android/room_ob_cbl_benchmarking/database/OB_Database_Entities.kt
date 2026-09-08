package com.bignerdranch.android.room_ob_cbl_benchmarking.database

import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne

// defining a fundamental Objectbox database for benchmarking
@Entity
data class EntryOb_B(
    @Id
    var id: Long = 0,
    var dateOb: String? = null,
    var entryOb: String? = null,
    var timeMinutesOb: Int? = null
) {
    @Backlink(to = "entryob_b")
    lateinit var entryattachmentob_b: ToMany<EntryAttachmentOb_B>

    lateinit var extradataob_b: ToOne<ExtraDataOb_B>
}

@Entity
data class ExtraDataOb_B(
    @Id
    var id: Long = 0,
    var reminderTypeOb: String? = null,
    var repeatOb: String? = null,
    var repeatDetailsOb: String? = null
)

@Entity
data class EntryAttachmentOb_B(
    @Id
    var id: Long = 0,
    var fileNameOb: String = "",
    var mimeTypeOb: String = "",
    var fileSizeOb: Long = 0L,
    var uriPathOb: String = "",
    var dataAddedOb: Long = System.currentTimeMillis()
) {
    lateinit var entryob_b: ToOne<EntryOb_B>
}