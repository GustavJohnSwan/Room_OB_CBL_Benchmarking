package com.bignerdranch.android.room_ob_cbl_benchmarking

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.DAO.OB_DAO
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryOb_B
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.MyObjectBox
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID
import org.junit.Assert.assertNull

import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.data_mapping.OB_Mapping
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.JsonAssetReader
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.JsonAssetDeserializer
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.JsonIDsAssetDeserializer

@RunWith(AndroidJUnit4::class)
class ObjectBoxCrudTest {

    @Test
    fun insertAndReadMainEntry() {
        val context =
            InstrumentationRegistry.getInstrumentation().targetContext

        val directory = File(
            context.cacheDir,
            "ob-test-${UUID.randomUUID()}"
        )

        val store = MyObjectBox.builder()
            .androidContext(context)
            .directory(directory)
            .build()

        try {
            val dao = OB_DAO(store)

            val entry = EntryOb_B(
                dateOb = "2026-08-27",
                entryOb = "Test entry",
                timeMinutesOb = 600
            )

            assertEquals(0L, dao.countEntries())

            dao.putMainEntry(entry)

            assertTrue(entry.id > 0L)
            assertEquals(1L, dao.countEntries())

            val savedEntry = dao.getMainEntry(entry.id)

            assertNotNull(savedEntry)
            assertEquals(entry.id, savedEntry!!.id)
            assertEquals(entry.dateOb, savedEntry.dateOb)
            assertEquals(entry.entryOb, savedEntry.entryOb)
            assertEquals(entry.timeMinutesOb, savedEntry.timeMinutesOb)

            // UPDATE the existing entry.
            val replacement = EntryOb_B(
                id = entry.id,
                dateOb = "2026-10-19",
                entryOb = "Updated entry",
                timeMinutesOb = 900
            )

            dao.updateEntry(replacement)

            val updatedEntry = dao.getMainEntry(entry.id)

            assertNotNull(updatedEntry)
            assertEquals(1L, dao.countEntries())
            assertEquals(replacement.id, updatedEntry!!.id)
            assertEquals(replacement.dateOb, updatedEntry.dateOb)
            assertEquals(replacement.entryOb, updatedEntry.entryOb)
            assertEquals(replacement.timeMinutesOb, updatedEntry.timeMinutesOb)

// DELETE the entry.
            assertTrue(dao.deleteMainEntry(entry.id))
            assertNull(dao.getMainEntry(entry.id))
            assertEquals(0L, dao.countEntries())
        } finally {
            store.close()
            directory.deleteRecursively()
        }
    }

    @Test
    fun bulkCrudMainEntries() {
        val context =
            InstrumentationRegistry.getInstrumentation().targetContext

        val directory = File(
            context.cacheDir,
            "ob-test-${UUID.randomUUID()}"
        )

        val store = MyObjectBox.builder()
            .androidContext(context)
            .directory(directory)
            .build()

        try {
            val dao = OB_DAO(store)

            // INSERT three distinct entries.
            val entries = listOf(
                EntryOb_B(dateOb = "2026-10-01", entryOb = "First", timeMinutesOb = 100),
                EntryOb_B(dateOb = "2026-10-02", entryOb = "Second", timeMinutesOb = 200),
                EntryOb_B(dateOb = "2026-10-03", entryOb = "Third", timeMinutesOb = 300)
            )

            dao.putMainEntries(entries)

            assertEquals(3L, dao.countEntries())
            assertTrue(entries.all { it.id > 0L })
            assertEquals(3, entries.map { it.id }.distinct().size)

            // GET the first and third entries.
            val selectedEntries = listOf(entries[0], entries[2])
            val selectedIds = selectedEntries.map { it.id }

            val fetched = dao.getMainEntries(selectedIds)

            assertEquals(2, fetched.size)
            assertEquals(selectedIds.toSet(), fetched.map { it.id }.toSet())

            val fetchedById = fetched.associateBy { it.id }

            for (expected in selectedEntries) {
                val actual = fetchedById.getValue(expected.id)
                assertEquals(expected.dateOb, actual.dateOb)
                assertEquals(expected.entryOb, actual.entryOb)
                assertEquals(expected.timeMinutesOb, actual.timeMinutesOb)
            }

            // UPDATE the selected entries.
            val replacements = selectedEntries.mapIndexed { index, entry ->
                EntryOb_B(
                    id = entry.id,
                    dateOb = "2026-10-19",
                    entryOb = "Updated $index",
                    timeMinutesOb = 800 + index
                )
            }

            dao.updateEntries(replacements)

            assertEquals(3L, dao.countEntries())

            val updated = dao.getMainEntries(selectedIds)
            assertEquals(2, updated.size)

            val updatedById = updated.associateBy { it.id }

            for (expected in replacements) {
                val actual = updatedById.getValue(expected.id)
                assertEquals(expected.dateOb, actual.dateOb)
                assertEquals(expected.entryOb, actual.entryOb)
                assertEquals(expected.timeMinutesOb, actual.timeMinutesOb)
            }

            // The unselected entry must remain unchanged.
            val untouched = dao.getMainEntry(entries[1].id)
            assertNotNull(untouched)
            assertEquals(entries[1].dateOb, untouched!!.dateOb)
            assertEquals(entries[1].entryOb, untouched.entryOb)
            assertEquals(entries[1].timeMinutesOb, untouched.timeMinutesOb)

            // DELETE the selected entries.
            dao.deleteMainEntries(selectedIds)

            assertEquals(1L, dao.countEntries())
            assertNull(dao.getMainEntry(entries[0].id))
            assertNull(dao.getMainEntry(entries[2].id))

            // GET ALL should return only the untouched entry.
            val remaining = dao.getAllMainEntries()

            assertEquals(1, remaining.size)
            assertEquals(entries[1].id, remaining[0].id)
            assertEquals(entries[1].dateOb, remaining[0].dateOb)
            assertEquals(entries[1].entryOb, remaining[0].entryOb)
            assertEquals(entries[1].timeMinutesOb, remaining[0].timeMinutesOb)

            // DELETE ALL should leave the database empty.
            dao.deleteAllMainEntries()

            assertEquals(0L, dao.countEntries())
            assertTrue(dao.getAllMainEntries().isEmpty())
        } finally {
            store.close()
            directory.deleteRecursively()
        }
    }

    @Test
    fun mainDatasetMatchesJsonAndPredefinedIds() {
        val context =
            InstrumentationRegistry.getInstrumentation().targetContext

        val directory = File(
            context.cacheDir,
            "ob-test-${UUID.randomUUID()}"
        )

        val store = MyObjectBox.builder()
            .androidContext(context)
            .directory(directory)
            .build()

        try {
            val dao = OB_DAO(store)
            val reader = JsonAssetReader(context)

            // Load the actual 100-entry dataset.
            val events = JsonAssetDeserializer().deserializeJson(
                reader.loadJsonFromAssets("events_A100_S1.json")
            )

            assertEquals(100, events.size)

            // Use your existing main-only mapper and DAO.
            val entries = OB_Mapping(store).mapMainEntries(events)
            dao.putMainEntries(entries)

            assertEquals(100L, dao.countEntries())

            // Verify every stored entry against its JSON insertion position.
            val storedEntries = dao.getAllMainEntries()
            assertEquals(100, storedEntries.size)

            val storedById = storedEntries.associateBy { it.id }
            val expectedIds = (1L..100L).toSet()

            assertEquals(expectedIds, storedById.keys)

            events.forEachIndexed { index, event ->
                val expectedId = index.toLong() + 1L
                val actual = storedById.getValue(expectedId)

                assertEquals(event.date, actual.dateOb)
                assertEquals(event.title, actual.entryOb)
                assertEquals(event.time, actual.timeMinutesOb)
                assertEquals(0L, actual.extradataob_b.targetId)
            }

            // Main-only insertion must not create ExtraData objects.
            assertEquals(0L, dao.EDOBBox.count())

            // Load the ID file matching the current database size.
            val databaseSize = dao.countEntries()

            val ids = JsonIDsAssetDeserializer().deserializeIDsJson(
                reader.loadJsonFromAssets("IDs_A${databaseSize}.json")
            )

            // The file must contain every ID exactly once.
            assertEquals(100, ids.size)
            assertEquals(expectedIds, ids.toSet())

            // Retrieve a subset in the predefined shuffled order.
            val selectedIds = ids.take(10)
            val selectedEntries = dao.getMainEntries(selectedIds)

            assertEquals(10, selectedEntries.size)
            assertEquals(
                selectedIds.toSet(),
                selectedEntries.map { it.id }.toSet()
            )

            // Compare by ID, without assuming the returned list's order.
            for (actual in selectedEntries) {
                val expected = events[(actual.id - 1L).toInt()]

                assertEquals(expected.date, actual.dateOb)
                assertEquals(expected.title, actual.entryOb)
                assertEquals(expected.time, actual.timeMinutesOb)
            }
        } finally {
            store.close()
            directory.deleteRecursively()
        }
    }
}