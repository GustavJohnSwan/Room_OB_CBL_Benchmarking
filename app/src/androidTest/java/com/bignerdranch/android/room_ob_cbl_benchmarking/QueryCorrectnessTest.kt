package com.bignerdranch.android.room_ob_cbl_benchmarking

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.DAO.*
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.data_mapping.*
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.helper_classes.Room.Room_EntryWithExtraData
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.helper_classes.ObjectBox.OB_EntryWithExtraData
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.*
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.MyObjectBox
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.database_setup.AppDatabase
import com.couchbase.lite.CouchbaseLite
import com.couchbase.lite.Database
import io.objectbox.BoxStore
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import java.util.UUID

// Test-only normalization: production DAO return types are unchanged.
// Each test runs separately for Room, ObjectBox and Couchbase Lite.
@RunWith(Parameterized::class)
class QueryCorrectnessTest(private val engine: String) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun engines(): Collection<Array<String>> = listOf(
            arrayOf("Room"), arrayOf("ObjectBox"), arrayOf("CBL")
        )
    }

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val name = "query-test-${UUID.randomUUID()}"
    private val obDirectory = File(context.cacheDir, name)
    private var roomDatabase: AppDatabase? = null
    private var obStore: BoxStore? = null
    private var cblDatabase: Database? = null
    private lateinit var room: Room_DAO
    private lateinit var ob: OB_DAO
    private lateinit var cbl: CBL_DAO

    private data class Extra(val reminder: String?, val repeat: String?, val details: String?)
    private data class Entry(
        val id: Long, val date: String?, val title: String?, val time: Int?, val extra: Extra?
    )
    private val chronological = compareBy<Entry> { it.date }.thenBy { it.time }.thenBy { it.id }
    private val start = "2026-01-02"
    private val end = "2026-01-03"

    @Before
    fun setUp() {
        when (engine) {
            "Room" -> {
                val db = Room.databaseBuilder(context, AppDatabase::class.java, "$name.db").build()
                roomDatabase = db
                room = db.Room_DAO()
            }
            "ObjectBox" -> {
                val store = MyObjectBox.builder().androidContext(context).directory(obDirectory).build()
                obStore = store
                ob = OB_DAO(store)
            }
            "CBL" -> {
                CouchbaseLite.init(context)
                val db = Database(name)
                cblDatabase = db
                db.createCollection("entries")
                db.createCollection("extra_data")
                cbl = CBL_DAO(db)
            }
        }
    }

    @After
    fun tearDown() {
        roomDatabase?.close()
        if (roomDatabase != null) context.deleteDatabase("$name.db")
        obStore?.close()
        if (obStore != null) obDirectory.deleteRecursively()
        cblDatabase?.delete()
    }

    private fun fixtures(): List<GeneratedEvent> {
        fun extra(reminder: String?, repeat: String?, details: String? = "details") =
            GeneratedExtraData(reminder, repeat, details)
        fun event(id: Int, date: String, time: Int, title: String, x: GeneratedExtraData?) =
            GeneratedEvent(1000 + id, date, title, time, x) // fixtureId deliberately differs from DB ID
        return listOf(
            event(1, "2026-01-01", 900, "Before range", extra("Alarm", "Weekly")),
            event(2, start, 600, "A_50% !@#\$+", extra("Alarm", "Daily")),
            event(3, start, 480, "No extra", null),
            event(4, start, 610, "a_50% !@#\$+", extra(null, "Daily")),
            event(5, end, 30, "AX50anything", extra("Alarm", "Weekly")),
            event(6, "2026-01-04", 1, "After range", extra("Alarm", "Weekly")),
            event(7, start, 620, "A_50% !@#\$+", extra(null, "Weekly")),
            event(8, start, 630, "All extra fields null", extra(null, null, null)),
            event(9, end, 900, "Other reminder", extra("Other", "Weekly")),
            event(10, start, 600, "Tie with ID 2", extra("Alarm", "Daily")),
            event(11, start, 620, "Tie with ID 7", extra(null, "Daily")),
            event(12, end, 700, "A_50% !@#\$+", null)
        )
    }

    private suspend fun seed(events: List<GeneratedEvent>): List<Entry> {
        when (engine) {
            "Room" -> room.insertEntries(Room_Mapping().map(events))
            "ObjectBox" -> ob.putEntries(OB_Mapping(requireNotNull(obStore)).map(events))
            "CBL" -> cbl.insertDocuments(CBL_Mapping().map(events, 1L))
        }
        // Expected data comes directly from source input, not from a database mapper or query.
        return events.mapIndexed { index, e ->
            Entry(index + 1L, e.date, e.title, e.time,
                e.extraData?.let { Extra(it.reminderType, it.repeatType, it.repeatDetails) })
        }
    }

    private fun fromRoom(rows: List<Room_EntryWithExtraData>) = rows.map {
        Entry(it.entry.id, it.entry.dateDB, it.entry.entryDB, it.entry.timeMinutes,
            it.extraData?.let { x -> Extra(x.reminderType, x.repeat, x.repeatDetails) })
    }
    private fun fromOb(rows: List<OB_EntryWithExtraData>) = rows.map {
        Entry(it.entry.id, it.entry.dateOb, it.entry.entryOb, it.entry.timeMinutesOb,
            it.extraData?.let { x -> Extra(x.reminderTypeOb, x.repeatOb, x.repeatDetailsOb) })
    }
    private fun fromCbl(rows: List<Map<String, Any?>>) = rows.map { row ->
        val main = row["entries"] as Map<*, *>
        val extra = row["extraData"] as? Map<*, *>
        val id = (row["entryId"] as String).toLong()
        if (extra != null) assertEquals("CBL ExtraData reference", id.toString(), extra["entry_id"])
        Entry(id, main["date"] as String?, main["entry"] as String?,
            (main["time_minutes"] as? Number)?.toInt(),
            extra?.let { Extra(it["reminder_type"] as String?, it["repeat"] as String?, it["repeat_details"] as String?) })
    }

    // Dispatch to the actual production DAO functions. No query is reimplemented here.
    private suspend fun rows(
        kind: String, a: String = start, b: String = end,
        limit: Int = 2, offset: Int = 0, text: String = "Alarm", floor: Int = 600
    ): List<Entry> = when (engine) {
        "Room" -> fromRoom(when (kind) {
            "date" -> room.findEntriesInSpecificDate(a)
            "range" -> room.findEntriesInDateRange(a, b)
            "next" -> room.findNextEntries(a, b, limit)
            "reminder" -> room.findEntriesWithSpecificReminder(text)
            "recurrence" -> room.findEntriesWithRecurrence()
            "combined" -> room.findEntriesInDateRangeReminderRepeat1OrRepeat2(a, b, text, "Daily", "Weekly", limit)
            "page" -> room.findEntriesReminderNullRepeatNotNullLimitOffset(limit, offset)
            "text" -> room.findEntriesContainsSpecificTextTimeIsLaterThanSpecifiedTime(text, floor)
            else -> error(kind)
        })
        "ObjectBox" -> fromOb(when (kind) {
            "date" -> ob.findEntriesInSpecificDate(a)
            "range" -> ob.findEntriesInDateRange(a, b)
            "next" -> ob.findNextEntries(a, b, limit.toLong())
            "reminder" -> ob.findEntriesWithSpecificReminder(text)
            "recurrence" -> ob.findEntriesWithRecurrance()
            "combined" -> ob.findEntriesInDateRangeReminderRepeat1OrRepeat2(a, b, text, "Daily", "Weekly", limit.toLong())
            "page" -> ob.findEntriesReminderNullRepeatNotNullLimitOffset(limit.toLong(), offset.toLong())
            "text" -> ob.findEntriesContainsSpecificTextTimeIsLaterThanSpecifiedTime(text, floor)
            else -> error(kind)
        })
        "CBL" -> fromCbl(when (kind) {
            "date" -> cbl.findDocumentsInSpecificDate(a)
            "range" -> cbl.findDocumentsInDateRange(a, b)
            "next" -> cbl.findNextDocument(a, b, limit)
            "reminder" -> cbl.findDocumentsWithSpecificReminder(text)
            "recurrence" -> cbl.findDocumentsWithReccurence()
            "combined" -> cbl.findDocInDateRangeReminderRepeatOrRepeat(a, b, text, "Daily", "Weekly", limit)
            "page" -> cbl.findDocumentsReminderNullRepeatNotNullLimitOffset(limit, offset)
            "text" -> cbl.findDocTitleLikeTextTimeLater(text, floor)
            else -> error(kind)
        })
        else -> error(engine)
    }

    private fun sameRows(label: String, expected: List<Entry>, actual: List<Entry>, ordered: Boolean = false) {
        // Sorting by ID only for comparison preserves duplicate rows, unlike converting to a Set.
        assertEquals("$engine: $label", if (ordered) expected else expected.sortedBy { it.id },
            if (ordered) actual else actual.sortedBy { it.id })
    }
    private fun inRange(e: Entry) = requireNotNull(e.date) in start..end

    @Test fun specificDateIncludesOptionalExtraData() = runBlocking {
        val expected = seed(fixtures()).filter { it.date == start }
        val actual = rows("date")
        sameRows("specific date", expected, actual)
        assertEquals("time order", actual.map { it.time }.sortedBy { it }, actual.map { it.time })
    }

    @Test fun dateRangeIncludesBothBoundaries() = runBlocking {
        val expected = seed(fixtures()).filter(::inRange)
        val actual = rows("range")
        sameRows("inclusive range", expected, actual)
        // ID order is not required for unlimited queries when date and time tie.
        assertEquals("date/time order", actual.sortedWith(chronological).map { it.date to it.time },
            actual.map { it.date to it.time })
    }

    @Test fun nextEntriesUseNumericIdTieBreaker() = runBlocking {
        val all = seed(fixtures()).filter { inRange(it) && it.extra?.reminder != null }.sortedWith(chronological)
        for (limit in listOf(1, 2, 20)) sameRows("next limit=$limit", all.take(limit), rows("next", limit = limit), true)
    }

    @Test fun specificReminderMatches() = runBlocking {
        val all = seed(fixtures())
        for (reminder in listOf("Alarm", "Other", "absent")) {
            sameRows("reminder=$reminder", all.filter { it.extra?.reminder == reminder }, rows("reminder", text = reminder))
        }
    }

    @Test fun recurrenceExcludesNullAndMissingExtraData() = runBlocking {
        val all = seed(fixtures())
        sameRows("recurrence", all.filter { it.extra?.repeat != null }, rows("recurrence"))
    }

    @Test fun combinedConditionsAndLimit() = runBlocking {
        val all = seed(fixtures()).filter {
            inRange(it) && it.extra?.reminder == "Alarm" && it.extra?.repeat in listOf("Daily", "Weekly")
        }.sortedWith(chronological)
        for (limit in listOf(1, 2, 20)) sameRows("combined limit=$limit", all.take(limit), rows("combined", limit = limit), true)
    }

    @Test fun paginationUsesStableOrderAndOffset() = runBlocking {
        val all = seed(fixtures()).filter { it.extra?.reminder == null && it.extra?.repeat != null }.sortedWith(chronological)
        for (offset in listOf(0, 1, 2, 20)) {
            sameRows("offset=$offset", all.drop(offset).take(2), rows("page", offset = offset), true)
        }
    }

    private suspend fun counts(): List<Pair<String?, Long>> = when (engine) {
        "Room" -> room.countEntriesByRepeatType().map { it.repeatType to it.repeatCount }
        "ObjectBox" -> ob.countEntriesByRepeatType().entries.map { it.key to it.value }
        "CBL" -> cbl.countDocumentsByRepeatType().map { it["repeat"] as String? to (it["repeatCount"] as Number).toLong() }
        else -> error(engine)
    }

    @Test fun repeatCountsAndOrdering() = runBlocking {
        val all = seed(fixtures())
        val expected = all.mapNotNull { it.extra?.repeat }.groupingBy { it }.eachCount()
            .toSortedMap().map { it.key to it.value.toLong() }
        assertEquals("$engine grouped counts", expected, counts())
    }

    private suspend fun minMax(a: String, b: String, reminder: String): Pair<Long?, Long?> = when (engine) {
        "Room" -> room.findEarliestEventsInRangeWithReminder(a, b, reminder).let { it.minTime to it.maxTime }
        "ObjectBox" -> ob.findEarliestLatestEventTimesInRangeWithReminder(a, b, reminder).let { it.minTime to it.maxTime }
        "CBL" -> cbl.findEarliestDocumentsInRangeWithReminder(a, b, reminder).let {
            // An absent aggregate value and explicit null both mean no numeric result.
            (it?.get("minTime") as? Number)?.toLong() to (it?.get("maxTime") as? Number)?.toLong()
        }
        else -> error(engine)
    }

    @Test fun minMaxMeansTimeOfDayNotEarliestDate() = runBlocking {
        val times = seed(fixtures()).filter { inRange(it) && it.extra?.reminder == "Alarm" }.map { requireNotNull(it.time).toLong() }
        assertEquals("$engine time of day", times.minOrNull() to times.maxOrNull(), minMax(start, end, "Alarm"))
    }

    @Test fun minMaxWithoutMatchesIsNull() = runBlocking {
        seed(fixtures())
        assertEquals("$engine no matching aggregate", null to null, minMax(start, end, "absent"))
    }

    @Test fun textIsLiteralCaseSensitiveWithInclusiveTimeFloor() = runBlocking {
        val all = seed(fixtures())
        for (fragment in listOf("A_50%", "a_50%", "_", "%", "!@#\$+", "No extra", "absent")) {
            for (floor in listOf(480, 600, 601)) {
                val expected = all.filter { requireNotNull(it.title).contains(fragment) && requireNotNull(it.time) >= floor }
                sameRows("fragment=$fragment floor=$floor", expected, rows("text", text = fragment, floor = floor))
            }
        }
    }

    @Test fun noMatchingRows() = runBlocking {
        seed(fixtures())
        for (kind in listOf("date", "range", "next", "combined")) {
            sameRows("no matches: $kind", emptyList(), rows(kind, a = "2030-01-01", b = "2030-01-02"))
        }
    }

    @Test fun emptyDatabaseResults() = runBlocking {
        for (kind in listOf("date", "range", "next", "reminder", "recurrence", "combined", "page", "text")) {
            sameRows("empty: $kind", emptyList(), rows(kind))
        }
        assertTrue("$engine empty grouping", counts().isEmpty())
        assertEquals("$engine empty aggregate", null to null, minMax(start, end, "Alarm"))
    }

    @Test fun fullJsonDatasetPreservesAllMainAndExtraFields() = runBlocking {
        val json = JsonAssetReader(context).loadJsonFromAssets("events_A100_S1.json")
        val events = JsonAssetDeserializer().deserializeJson(json)
        assertEquals(100, events.size)
        val expected = seed(events)
        val firstDate = events.minOf { it.date }
        val lastDate = events.maxOf { it.date }
        val actual = rows("range", a = firstDate, b = lastDate)
        sameRows("full JSON data including ExtraData", expected, actual)
        assertEquals("date/time order", actual.sortedWith(chronological).map { it.date to it.time },
            actual.map { it.date to it.time })
    }
}
