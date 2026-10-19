package com.bignerdranch.android.room_ob_cbl_benchmarking

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.DAO.CBL_DAO
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.data_mapping.CBL_Mapping
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.JsonAssetReader
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.JsonAssetDeserializer
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.JsonIDsAssetDeserializer
import com.couchbase.lite.CouchbaseLite
import com.couchbase.lite.Database
import com.couchbase.lite.MutableDocument
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class CblCrudTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var database: Database
    private lateinit var dao: CBL_DAO

    @Before
    fun setUp() {
        CouchbaseLite.init(context)
        database = Database("cbl-test-${UUID.randomUUID()}")
        database.createCollection("entries")
        database.createCollection("extra_data")
        dao = CBL_DAO(database)
    }

    @After
    fun tearDown() {
        // Instance delete removes and closes this test's unique database.
        if (::database.isInitialized) database.delete()
    }

    private fun document(id: String, date: String, title: String, time: Int): MutableDocument =
        MutableDocument(id)
            .setString("date", date)
            .setString("entry", title)
            .setInt("time_minutes", time)

    private fun assertEntry(
        row: Map<String, Any?>?, id: String, date: String, title: String, time: Int
    ) {
        assertNotNull("Missing entry $id", row)
        val actual = requireNotNull(row)
        assertEquals(id, actual["entryId"])
        val fields = actual["entries"] as? Map<*, *>
        assertNotNull("Missing entries dictionary for $id", fields)
        val data = requireNotNull(fields)
        assertEquals(date, data["date"])
        assertEquals(title, data["entry"])
        // Fleece may return different JVM Number subclasses.
        assertTrue(data["time_minutes"] is Number)
        assertEquals(time.toLong(), (data["time_minutes"] as Number).toLong())
    }

    @Test
    fun singleCrudMainEntry() {
        assertEquals(0L, dao.countDocuments())
        dao.insertMainDoc(document("1", "2026-10-01", "First", 100))
        assertEquals(1L, dao.countDocuments())
        assertEntry(dao.getDocumentById("1"), "1", "2026-10-01", "First", 100)

        // Mirrors the current ViewModel: prepare a replacement with the same ID.
        dao.updateDocument(document("1", "2026-10-19", "Updated", 900))
        assertEquals(1L, dao.countDocuments())
        assertEntry(dao.getDocumentById("1"), "1", "2026-10-19", "Updated", 900)

        dao.deleteDocument("1")
        assertNull(dao.getDocumentById("1"))
        assertEquals(0L, dao.countDocuments())
    }

    @Test
    fun bulkCrudMainEntries() {
        dao.insertMainDocs(listOf(
            document("1", "2026-10-01", "First", 100),
            document("2", "2026-10-02", "Second", 200),
            document("3", "2026-10-03", "Third", 300)
        ))
        assertEquals(3L, dao.countDocuments())
        val selectedIds = listOf("1", "3")
        val fetched = dao.getDocumentsById(selectedIds)
        assertEquals(2, fetched.size)
        assertEquals(selectedIds.toSet(), fetched.map { it["entryId"] }.toSet())
        val byId = fetched.associateBy { it["entryId"] }
        assertEntry(byId["1"], "1", "2026-10-01", "First", 100)
        assertEntry(byId["3"], "3", "2026-10-03", "Third", 300)

        dao.updateDocuments(listOf(
            document("1", "2026-10-19", "Updated first", 800),
            document("3", "2026-10-19", "Updated third", 801)
        ))
        assertEquals(3L, dao.countDocuments())
        val updated = dao.getDocumentsById(selectedIds)
        assertEquals(2, updated.size)
        assertEquals(selectedIds.toSet(), updated.map { it["entryId"] }.toSet())
        val updatedById = updated.associateBy { it["entryId"] }
        assertEntry(updatedById["1"], "1", "2026-10-19", "Updated first", 800)
        assertEntry(updatedById["3"], "3", "2026-10-19", "Updated third", 801)
        assertEntry(dao.getDocumentById("2"), "2", "2026-10-02", "Second", 200)

        dao.deleteDocuments(selectedIds)
        assertEquals(1L, dao.countDocuments())
        selectedIds.forEach { assertNull(dao.getDocumentById(it)) }
        val remaining = dao.getAllDocuments()
        assertEquals(1, remaining.size)
        assertEntry(remaining.single(), "2", "2026-10-02", "Second", 200)

        dao.deleteAllDocuments()
        assertEquals(0L, dao.countDocuments())
        assertTrue(dao.getAllDocuments().isEmpty())
    }

    @Test
    fun mainDatasetMatchesJsonAndPredefinedIds() {
        val reader = JsonAssetReader(context)
        val events = JsonAssetDeserializer().deserializeJson(
            reader.loadJsonFromAssets("events_A100_S1.json")
        )
        assertEquals(100, events.size)
        assertEquals(1L, dao.findNextEntryId())
        dao.insertMainDocs(CBL_Mapping().mapMainEntries(events, dao.findNextEntryId()))
        assertEquals(100L, dao.countDocuments())
        assertEquals(101L, dao.findNextEntryId())
        assertEquals(0L, requireNotNull(database.getCollection("extra_data")).count)

        val all = dao.getAllDocuments()
        assertEquals(100, all.size)
        assertEquals((1..100).map { it.toString() }.toSet(), all.map { it["entryId"] }.toSet())
        val byId = all.associateBy { it["entryId"] }
        events.forEachIndexed { index, event ->
            val id = (index + 1).toString()
            assertEntry(byId[id], id, event.date, event.title, event.time)
        }

        val fileIds = JsonIDsAssetDeserializer().deserializeIDsJson(
            reader.loadJsonFromAssets("IDs_A${dao.countDocuments()}.json")
        )
        assertEquals(100, fileIds.size)
        assertEquals((1L..100L).toSet(), fileIds.toSet())
        val selectedIds = fileIds.take(10).map { it.toString() }
        val selected = dao.getDocumentsById(selectedIds)
        assertEquals(10, selected.size)
        assertEquals(selectedIds.toSet(), selected.map { it["entryId"] }.toSet())
        for (row in selected) {
            val id = row["entryId"] as String
            val event = events[id.toInt() - 1]
            assertEntry(row, id, event.date, event.title, event.time)
        }
    }
}
