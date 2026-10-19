package com.bignerdranch.android.room_ob_cbl_benchmarking

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.DAO.Room_DAO
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.data_mapping.Room_Mapping
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.JsonAssetReader
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.JsonAssetDeserializer
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.JsonIDsAssetDeserializer
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryTable
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.database_setup.AppDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class RoomCrudTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var database: AppDatabase
    private lateinit var dao: Room_DAO
    private lateinit var databaseName: String

    @Before
    fun setUp() {
        databaseName = "room-test-${UUID.randomUUID()}.db"
        database = Room.databaseBuilder(context, AppDatabase::class.java, databaseName).build()
        dao = database.Room_DAO()
    }

    @After
    fun tearDown() {
        if (::database.isInitialized) database.close()
        if (::databaseName.isInitialized) context.deleteDatabase(databaseName)
    }

    @Test
    fun singleCrudMainEntry() = runBlocking {
        assertEquals(0L, dao.countEntries())
        val input = EntryTable(dateDB = "2026-10-01", entryDB = "First", timeMinutes = 100)
        val id = dao.insertMainEntry(input)
        val expected = input.copy(id = id)
        assertTrue(id > 0L)
        assertEquals(1L, dao.countEntries())
        assertEquals(expected, dao.getSpecificEntry(id))

        val replacement = expected.copy(dateDB = "2026-10-19", entryDB = "Updated", timeMinutes = 900)
        dao.updateEntry(replacement)
        assertEquals(1L, dao.countEntries())
        assertEquals(replacement, dao.getSpecificEntry(id))

        assertEquals(1, dao.deleteEntry(replacement))
        assertNull(dao.getSpecificEntry(id))
        assertEquals(0L, dao.countEntries())
    }

    @Test
    fun bulkCrudMainEntries() = runBlocking {
        val inputs = listOf(
            EntryTable(dateDB = "2026-10-01", entryDB = "First", timeMinutes = 100),
            EntryTable(dateDB = "2026-10-02", entryDB = "Second", timeMinutes = 200),
            EntryTable(dateDB = "2026-10-03", entryDB = "Third", timeMinutes = 300)
        )
        val ids = dao.insertMainEntries(inputs)
        assertEquals(3, ids.size)
        assertEquals(3, ids.toSet().size)
        assertTrue(ids.all { it > 0L })
        assertEquals(3L, dao.countEntries())
        val entries = inputs.mapIndexed { index, entry -> entry.copy(id = ids[index]) }
        val selected = listOf(entries[0], entries[2])
        val selectedIds = selected.map { it.id }
        val fetched = dao.getEntriesByIDs(selectedIds)
        assertEquals(2, fetched.size)
        assertEquals(selected.toSet(), fetched.toSet())

        val replacements = selected.mapIndexed { index, entry ->
            entry.copy(dateDB = "2026-10-19", entryDB = "Updated $index", timeMinutes = 800 + index)
        }
        dao.updateEntries(replacements)
        assertEquals(3L, dao.countEntries())
        val updated = dao.getEntriesByIDs(selectedIds)
        assertEquals(2, updated.size)
        assertEquals(replacements.toSet(), updated.toSet())
        assertEquals(entries[1], dao.getSpecificEntry(entries[1].id))

        assertEquals(2, dao.deleteEntries(replacements))
        assertEquals(1L, dao.countEntries())
        selectedIds.forEach { assertNull(dao.getSpecificEntry(it)) }
        assertEquals(listOf(entries[1]), dao.getAllEntriesBulk())

        dao.deleteAllEntries()
        assertEquals(0L, dao.countEntries())
        assertTrue(dao.getAllEntriesBulk().isEmpty())
    }

    @Test
    fun mainDatasetMatchesJsonAndPredefinedIds() = runBlocking {
        val reader = JsonAssetReader(context)
        val events = JsonAssetDeserializer().deserializeJson(
            reader.loadJsonFromAssets("events_A100_S1.json")
        )
        assertEquals(100, events.size)
        val inputs = Room_Mapping().mapMainEntries(events)
        val ids = dao.insertMainEntries(inputs)
        assertEquals((1L..100L).toList(), ids)
        assertEquals(100L, dao.countEntries())

        // Construct expectations from the JSON, independently of the mapper.
        val expected = events.mapIndexed { index, event ->
            EntryTable(index.toLong() + 1L, event.date, event.title, event.time)
        }
        val all = dao.getAllEntriesBulk()
        assertEquals(100, all.size)
        assertEquals(expected.toSet(), all.toSet())

        database.openHelper.readableDatabase.query("SELECT COUNT(*) FROM ExtraDataTable").use {
            assertTrue(it.moveToFirst())
            assertEquals(0L, it.getLong(0))
        }

        val fileIds = JsonIDsAssetDeserializer().deserializeIDsJson(
            reader.loadJsonFromAssets("IDs_A${dao.countEntries()}.json")
        )
        assertEquals(100, fileIds.size)
        assertEquals((1L..100L).toSet(), fileIds.toSet())
        val selectedIds = fileIds.take(10)
        val selected = dao.getEntriesByIDs(selectedIds)
        assertEquals(10, selected.size)
        assertEquals(expected.filter { it.id in selectedIds }.toSet(), selected.toSet())
    }
}
