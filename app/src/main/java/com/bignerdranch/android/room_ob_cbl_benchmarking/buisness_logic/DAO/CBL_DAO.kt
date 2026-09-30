package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.DAO

import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.GeneratedEvent
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.database_setup.CouchbaseLiteProvider
import com.couchbase.lite.DataSource
import com.couchbase.lite.Expression
import com.couchbase.lite.Join
import com.couchbase.lite.Meta
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.Ordering
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import com.couchbase.lite.UnitOfWork
import kotlin.use

import com.couchbase.lite.Function

class CBL_DAO {

    // retrieving the database
    val database = CouchbaseLiteProvider.getDatabase()


    // retrieving collections for DAO functions to use
    val Collection_Entires = CouchbaseLiteProvider
        .getDatabase()
        .getCollection("entries")
        ?: throw IllegalStateException("collection not found")

    val Collection_ExtraData = CouchbaseLiteProvider
        .getDatabase()
        .getCollection("extra_data")
        ?: throw IllegalStateException("collection not found")

    // last page looked at (looking for basics on how to define documents :
    // https://docs.couchbase.com/couchbase-lite/current/android/document.html


    // helper variable that stores next ID value for CBL insertion
    private var nextEntryId: Long = findNextEntryId()

    // helper function that reads the CBL database to find out the current max ID
    // and then iterate it by 1 for next insertion (iteration done here once. After that DAO functions self iterate ID)
    private fun findNextEntryId(): Long {

        // makes the database available for use in the function
        val database = CouchbaseLiteProvider.getDatabase()

        // defines a query
        val query = database.createQuery(
            "SELECT IFMISSINGORNULL(MAX(TONUMBER(META().id)), 0) AS maxId FROM _default.entries"
        )


        // returns the query (result + 1L) OR (0L + 1L) if query result is null
        return query.execute().use { results ->
            (results.next()?.getLong("maxId") ?: 0L) + 1L
        }
    }


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // Basic CRUD

    // TO DO - WRITE THE CBL DAO IN THIS FILE


    // INSERT ENTRY
    fun insertDocument(event: GeneratedEvent) {
        database.inBatch(UnitOfWork {

        // creating a mutable document (entry) with ID iterator helper function
        val entryDoc = MutableDocument(nextEntryId.toString())

        entryDoc.setString("date", event.date)
        entryDoc.setString("entry", event.title)
        entryDoc.setInt("time_minutes", event.time)

        // store document in collection
        Collection_Entires.save(entryDoc)
        nextEntryId++

        // check if current event has extraData (not null), if it does, execute code in brackets
        event.extraData?.let { extraData ->

            // creating a mutable document (extra data)
            val extraDataDoc = MutableDocument()

            // define extra data parameters and main entry ID reference
            extraDataDoc.setString("entry_id", entryDoc.id)
            extraDataDoc.setString("reminder_type", extraData.reminderType)
            extraDataDoc.setString("repeat", extraData.repeatType)
            extraDataDoc.setString("repeat_details", extraData.repeatDetails)

            // store document in collection
            Collection_ExtraData.save(extraDataDoc)
        }

        })


    }


    // INSERT ENTRIES BULK
    fun insertDocuments(events: List<GeneratedEvent>) {

            database.inBatch(UnitOfWork {
                for (event in events) {
                    // creating a mutable document (entry) with ID iterator helper function
                    val entryDoc = MutableDocument(nextEntryId.toString())

                    entryDoc.setString("date", event.date)
                    entryDoc.setString("entry", event.title)
                    entryDoc.setInt("time_minutes", event.time)

                    // store document in collection
                    Collection_Entires.save(entryDoc)
                    nextEntryId++

                    // check if current event has extraData (not null), if it does, execute code in brackets
                    event.extraData?.let { extraData ->

                        // creating a mutable document (extra data)
                        val extraDataDoc = MutableDocument()

                        // define extra data parameters and main entry ID reference
                        extraDataDoc.setString("entry_id", entryDoc.id)
                        extraDataDoc.setString("reminder_type", extraData.reminderType)
                        extraDataDoc.setString("repeat", extraData.repeatType)
                        extraDataDoc.setString("repeat_details", extraData.repeatDetails)

                        // store document in collection
                        Collection_ExtraData.save(extraDataDoc)
                    }
                }
            })
    }




    // UPDATE DOCUMENT
    fun updateDocument(docId: String, event: GeneratedEvent) {

        database.inBatch(UnitOfWork {

            val entryDocument = Collection_Entires.getDocument(docId)

            if (entryDocument != null) {

                val queryExtraData = QueryBuilder
                    .select(
                        SelectResult.expression(Meta.id).`as`("extraDataId")
                    )
                    .from(DataSource.collection(Collection_ExtraData))
                    .where(
                        Expression.property("entry_id")
                            .equalTo(Expression.string(docId))
                    )

                val extraDataId = queryExtraData.execute().use { results ->
                    results.next()?.getString("extraDataId")
                }

                when {

                    event.extraData != null && extraDataId != null -> {

                        Collection_ExtraData.getDocument(extraDataId)?.toMutable()?.let {

                            it.setString("reminder_type", event.extraData?.reminderType)
                            it.setString("repeat", event.extraData?.repeatType)
                            it.setString("repeat_details", event.extraData?.repeatDetails)
                            Collection_ExtraData.save(it)

                        }
                    }

                    event.extraData == null && extraDataId == null -> {
                        // nothing should happen with extra data
                    }

                    event.extraData != null && extraDataId == null -> {

                        // creating a mutable document (extra data)
                        val extraDataDoc = MutableDocument()

                        // define extra data parameters and main entry ID reference
                        extraDataDoc.setString("entry_id", docId)
                        extraDataDoc.setString("reminder_type", event.extraData?.reminderType)
                        extraDataDoc.setString("repeat", event.extraData?.repeatType)
                        extraDataDoc.setString("repeat_details", event.extraData?.repeatDetails)

                        // store document in collection
                        Collection_ExtraData.save(extraDataDoc)


                    }

                    event.extraData == null && extraDataId != null -> {

                        val extraDataDocument =
                            Collection_ExtraData.getDocument(extraDataId)

                        if (extraDataDocument != null) {
                            Collection_ExtraData.delete(extraDataDocument)
                        }


                    }
                }


                entryDocument.toMutable().let {

                    it.setString("date", event.date)
                    it.setString("entry", event.title)
                    it.setInt("time_minutes", event.time)
                    Collection_Entires.save(it)

                }
            }
        })
    }


    // UPDATE DOCUMENTS
    fun updateDocuments(docIds: List<String>, events: List<GeneratedEvent>) {

        require(docIds.size == events.size)

        database.inBatch(UnitOfWork {

            for (i in docIds.indices) {

                val entryDocument = Collection_Entires.getDocument(docIds[i])

                if (entryDocument != null) {

                    val queryExtraData = QueryBuilder
                        .select(
                            SelectResult.expression(Meta.id).`as`("extraDataId")
                        )
                        .from(DataSource.collection(Collection_ExtraData))
                        .where(
                            Expression.property("entry_id")
                                .equalTo(Expression.string(docIds[i]))
                        )

                    val extraDataId = queryExtraData.execute().use { results ->
                        results.next()?.getString("extraDataId")
                    }

                    when {

                        events[i].extraData != null && extraDataId != null -> {

                            Collection_ExtraData.getDocument(extraDataId)?.toMutable()?.let {

                                it.setString("reminder_type", events[i].extraData?.reminderType)
                                it.setString("repeat", events[i].extraData?.repeatType)
                                it.setString("repeat_details", events[i].extraData?.repeatDetails)
                                Collection_ExtraData.save(it)

                            }
                        }

                        events[i].extraData == null && extraDataId == null -> {
                            // nothing should happen with extra data
                        }

                        events[i].extraData != null && extraDataId == null -> {

                            // creating a mutable document (extra data)
                            val extraDataDoc = MutableDocument()

                            // define extra data parameters and main entry ID reference
                            extraDataDoc.setString("entry_id", docIds[i])
                            extraDataDoc.setString("reminder_type", events[i].extraData?.reminderType)
                            extraDataDoc.setString("repeat", events[i].extraData?.repeatType)
                            extraDataDoc.setString("repeat_details", events[i].extraData?.repeatDetails)

                            // store document in collection
                            Collection_ExtraData.save(extraDataDoc)


                        }

                        events[i].extraData == null && extraDataId != null -> {

                            val extraDataDocument =
                                Collection_ExtraData.getDocument(extraDataId)

                            if (extraDataDocument != null) {
                                Collection_ExtraData.delete(extraDataDocument)
                            }


                        }
                    }


                    entryDocument.toMutable().let {

                        it.setString("date", events[i].date)
                        it.setString("entry", events[i].title)
                        it.setInt("time_minutes", events[i].time)
                        Collection_Entires.save(it)

                    }
                }
            }
        })
        
    }



    // COUNT DOCUMENTS
    fun countDocuments(): Long {

        val query = QueryBuilder
            .select(
                SelectResult.expression(
                    Function.count(
                        Expression.string("*"))).`as`("documentCount")
            )
            .from(DataSource.collection(Collection_Entires))


        return query.execute().use { results ->
            results.next()?.getLong("documentCount") ?: 0L
        }
    }






    // GET BULK
    fun getAllDocuments(): List<Map<String, Any?>> {
        val queryAll = QueryBuilder
            .select(
                SelectResult.expression(Meta.id.from("entries")).`as`("entryId"),
                SelectResult.all().from("entries"),
                SelectResult.all().from("extraData")
                )
            .from(DataSource.collection(Collection_Entires).`as`("entries"))
            .join(
                Join.leftJoin(DataSource.collection(Collection_ExtraData).`as`("extraData"))
                    .on(
                        Meta.id.from("entries")
                            .equalTo(Expression.property("entry_id").from("extraData"))
                    )
            )

        return queryAll.execute().use { results ->
            results.map { result -> result.toMap()}
        }
    }


    // GET document by Id
    fun getDocumentById(documentId: String): Map<String, Any?>? {
        val queryDocument = QueryBuilder
            .select(
                SelectResult.expression(Meta.id.from("entries")).`as`("entryId"),
                SelectResult.all().from("entries"),
                SelectResult.all().from("extraData")
            )
            .from(DataSource.collection(Collection_Entires).`as`("entries"))
            .join(
                Join.leftJoin(DataSource.collection(Collection_ExtraData).`as`("extraData"))
                    .on(
                        Meta.id.from("entries")
                            .equalTo(Expression.property("entry_id").from("extraData"))
                    )
            )
            .where(
                Meta.id.from("entries")
                .equalTo(
                    Expression.string(documentId)))

        return queryDocument.execute().use { results ->
            results.next()?.toMap()
        }
    }

    // GET documents by List of Ids
    fun getDocumentsById (documentIds: List<String>): List<Map<String, Any?>> {
        if (documentIds.isEmpty()) return emptyList()


        val idExpressions = documentIds
            .map { id -> Expression.string(id) }
            .toTypedArray()

        val queryAll = QueryBuilder
            .select(
                SelectResult.expression(Meta.id.from("entries")).`as`("entryId"),
                SelectResult.all().from("entries"),
                SelectResult.all().from("extraData")
            )
            .from(DataSource.collection(Collection_Entires).`as`("entries"))
            .join(
                Join.leftJoin(DataSource.collection(Collection_ExtraData).`as`("extraData"))
                    .on(
                        Meta.id.from("entries")
                            .equalTo(Expression.property("entry_id").from("extraData"))
                    )
            )
            .where(
                Meta.id.from("entries").`in`(*idExpressions)
            )

        return queryAll.execute().use { results ->
            results.map { result -> result.toMap() }
        }
    }



    // DELETE DOCUMENT by Id
    fun deleteDocument(documentId: String) {
        database.inBatch(UnitOfWork {
            // Find the optional ExtraData document's ID.
            val queryExtraData = QueryBuilder
                .select(
                    SelectResult.expression(Meta.id).`as`("extraDataId")
                )
                .from(DataSource.collection(Collection_ExtraData))
                .where(
                    Expression.property("entry_id")
                        .equalTo(Expression.string(documentId))
                )

            val extraDataId = queryExtraData.execute().use { results ->
                results.next()?.getString("extraDataId")
            }

            // Delete ExtraData if it exists.
            if (extraDataId != null) {
                val extraDataDocument =
                    Collection_ExtraData.getDocument(extraDataId)

                if (extraDataDocument != null) {
                    Collection_ExtraData.delete(extraDataDocument)
                }
            }

            // Delete the main entry if it exists.
            val entryDocument = Collection_Entires.getDocument(documentId)

            if (entryDocument != null) {
                Collection_Entires.delete(entryDocument)
            }
        })
    }



    // DELETE DOCUMENTS BY ids

    fun deleteDocuments(documentIds: List<String>) {
        if (documentIds.isEmpty()) return

        val idExpressions = documentIds
            .map { id -> Expression.string(id) }
            .toTypedArray()

        database.inBatch(UnitOfWork {
            // Find ExtraData belonging to the selected entries.
            val queryExtraData = QueryBuilder
                .select(
                    SelectResult.expression(Meta.id).`as`("extraDataId")
                )
                .from(DataSource.collection(Collection_ExtraData))
                .where(
                    Expression.property("entry_id")
                        .`in`(*idExpressions)
                )

            val extraDataIds = queryExtraData.execute().use { results ->
                results.mapNotNull { result ->
                    result.getString("extraDataId")
                }
            }

            // Delete the matching ExtraData documents.
            for (extraDataId in extraDataIds) {
                val extraDataDocument =
                    Collection_ExtraData.getDocument(extraDataId)

                if (extraDataDocument != null) {
                    Collection_ExtraData.delete(extraDataDocument)
                }
            }

            // Delete the selected main documents.
            for (documentId in documentIds) {
                val entryDocument =
                    Collection_Entires.getDocument(documentId)

                if (entryDocument != null) {
                    Collection_Entires.delete(entryDocument)
                }
            }
        })
    }


    // DELETE ALL DOCUMENTS
    fun deleteAllDocuments() {
        database.inBatch(UnitOfWork {
            for (collection in listOf(Collection_Entires, Collection_ExtraData)) {
                val query = QueryBuilder
                    .select(SelectResult.expression(Meta.id).`as`("documentId"))
                    .from(DataSource.collection(collection))

                val documentIds = query.execute().use { results ->
                    results.mapNotNull { result ->
                        result.getString("documentId")
                    }
                }

                for (documentId in documentIds) {
                    val document = collection.getDocument(documentId)

                    if (document != null) {
                        collection.delete(document)
                    }
                }
            }
        })
    }


    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // Medium Queries

    // Find documents in date, order by time in ObjectBox database
    fun findDocumentsInSpecificDate(date: String): List<Map<String, Any?>> {

        val queryAll = QueryBuilder
            .select(
                SelectResult.expression(Meta.id.from("entries")).`as`("entryId"),
                SelectResult.all().from("entries"),
                SelectResult.all().from("extraData")
            )
            .from(DataSource.collection(Collection_Entires).`as`("entries"))
            .join(
                Join.leftJoin(DataSource.collection(Collection_ExtraData).`as`("extraData"))
                    .on(
                        Meta.id.from("entries")
                            .equalTo(Expression.property("entry_id").from("extraData"))
                    )
            )
            .where(
                Expression.property("date").from("entries")
                    .equalTo(Expression.string(date)))
            .orderBy(
                Ordering.expression(
                    Expression.property("time_minutes").from("entries")
                ).ascending()
            )

        return queryAll.execute().use { results ->
            results.map { result -> result.toMap()}
        }

    }


    // Find documents in date range, order by time
    fun findDocumentsInDateRange(startDate: String, endDate: String): List<Map<String, Any?>> {

        val queryAll = QueryBuilder
            .select(
                SelectResult.expression(Meta.id.from("entries")).`as`("entryId"),
                SelectResult.all().from("entries"),
                SelectResult.all().from("extraData")
            )
            .from(DataSource.collection(Collection_Entires).`as`("entries"))
            .join(
                Join.leftJoin(DataSource.collection(Collection_ExtraData).`as`("extraData"))
                    .on(
                        Meta.id.from("entries")
                            .equalTo(Expression.property("entry_id").from("extraData"))
                    )
            )
            .where(Expression.property("date")
                .from("entries")
                .between(
                    Expression.string(startDate),
                    Expression.string(endDate)
                )
            )
            .orderBy(
                Ordering.expression(
                    Expression.property("date").from("entries")
                ).ascending(),

                Ordering.expression(
                    Expression.property("time_minutes").from("entries")
                ).ascending()
            )

        return queryAll.execute().use { results ->
            results.map { result -> result.toMap()}
        }

    }





    // FIND NEXT X DOCUMENTS FROM DATE (TODAY) TO DATE WITH REMINDER (NOT NULL)
    fun findNextDocument(thisLimit: Int, startDate: String, endDate: String): List<Map<String, Any?>> {

        val queryAll = QueryBuilder
        .select(
            SelectResult.expression(Meta.id.from("entries")).`as`("entryId"),
            SelectResult.all().from("entries"),
            SelectResult.all().from("extraData")
        )
            .from(DataSource.collection(Collection_Entires).`as`("entries"))
            .join(
                Join.leftJoin(DataSource.collection(Collection_ExtraData).`as`("extraData"))
                    .on(
                        Meta.id.from("entries")
                            .equalTo(Expression.property("entry_id").from("extraData"))
                    )
            )
            .where(Expression.property("date")
                .from("entries")
                .between(
                Expression.string(startDate),
                Expression.string(endDate)
            )
                .and(
                    Expression.property("reminder_type").from("extraData")
                        .isValued()
                )
            )
            .orderBy(
                Ordering.expression(
                    Expression.property("date").from("entries")
                ).ascending(),

                Ordering.expression(
                    Expression.property("time_minutes").from("entries")
                ).ascending(),

                Ordering.expression(
                    Function.length(Meta.id.from("entries"))
                ).ascending(),

                Ordering.expression(
                    Meta.id.from("entries")
                ).ascending()
            )
            .limit(
                Expression.intValue(thisLimit),
                Expression.intValue(0)
            )

        return queryAll.execute().use { results ->
            results.map { result -> result.toMap()}
        }

    }




    // Find documents with a specific reminder
    fun findDocumentsWithSpecificReminder(
        specificReminder: String
    ): List<Map<String, Any?>> {

        val queryAll = QueryBuilder
            .select(
                SelectResult.expression(Meta.id.from("entries")).`as`("entryId"),
                SelectResult.all().from("entries"),
                SelectResult.all().from("extraData")
            )
            .from(DataSource.collection(Collection_Entires).`as`("entries"))
            .join(
                Join.leftJoin(DataSource.collection(Collection_ExtraData).`as`("extraData"))
                    .on(
                        Meta.id.from("entries")
                            .equalTo(Expression.property("entry_id").from("extraData"))
                    )
            )
            .where(Expression.property("reminder_type")
                .from("extraData")
                .equalTo(Expression.string(specificReminder))
            )

        return queryAll.execute().use { results ->
            results.map { result -> result.toMap()}
        }

    }


    // FIND DOCUMENTS WITH ANY RECCURANCE
    fun findDocumentsWithReccurence(): List<Map<String, Any?>> {


        val queryAll = QueryBuilder
            .select(
                SelectResult.expression(Meta.id.from("entries")).`as`("entryId"),
                SelectResult.all().from("entries"),
                SelectResult.all().from("extraData")
            )
            .from(DataSource.collection(Collection_Entires).`as`("entries"))
            .join(
                Join.leftJoin(DataSource.collection(Collection_ExtraData).`as`("extraData"))
                    .on(
                        Meta.id.from("entries")
                            .equalTo(Expression.property("entry_id").from("extraData"))
                    )
            )
            .where(Expression.property("repeat")
                .from("extraData")
                .isValued()
            )


        return queryAll.execute().use { results ->
            results.map { result -> result.toMap()}
        }


    }



    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________

    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // _____________________________________________________________________________________________
    // ADVANCED QUERIES

    // FIND documents in date range with specific Reminder and specific Repeat1 OR Repeat2
    fun findDocInDateRangeReminderRepeatOrRepeat(
        startDate: String,
        endDate: String,
        specificReminder: String,
        specificRepeat1: String,
        specificRepeat2: String,
        limit: Int
    ): List<Map<String, Any?>> {
        val query = QueryBuilder
            .select(
                SelectResult.expression(Meta.id.from("entries")).`as`("entryId"),
                SelectResult.all().from("entries"),
                SelectResult.all().from("extraData")
            )
            .from(DataSource.collection(Collection_Entires).`as`("entries"))
            .join(
                Join.innerJoin(DataSource.collection(Collection_ExtraData).`as`("extraData"))
                    .on(
                        Meta.id.from("entries")
                            .equalTo(Expression.property("entry_id").from("extraData"))
                    )
            )
            .where(Expression.property("date")
                .from("entries")
                .between(
                    Expression.string(startDate),
                    Expression.string(endDate)
                )
                .and(
                    Expression.property("reminder_type")
                    .from("extraData")
                    .equalTo(Expression.string(specificReminder))
                )
                .and(
                    Expression.property("repeat")
                        .from("extraData")
                        .equalTo(Expression.string(specificRepeat1))
                        .or(
                            Expression.property("repeat")
                                .from("extraData")
                                .equalTo(Expression.string(specificRepeat2))
                        )
                )
            )
            .orderBy(
                Ordering.expression(
                    Expression.property("date").from("entries")
                ).ascending(),

                Ordering.expression(
                    Expression.property("time_minutes").from("entries")
                ).ascending(),

                Ordering.expression(
                    Function.length(Meta.id.from("entries"))
                ).ascending(),

                Ordering.expression(
                    Meta.id.from("entries")
                ).ascending()
            )
            .limit(
                Expression.intValue(limit),
                Expression.intValue(0)
            )

        return query.execute().use { results ->
            results.map { result -> result.toMap()}
        }
    }




    // FIND documents with Reminder IS NULL and Repeat IS NOT NULL + LIMIT + OFFSET
    fun findDocumentsReminderNullRepeatNotNullLimitOffset (
        limit: Int,
        offset: Int
    ): List<Map<String, Any?>> {

        val query = QueryBuilder
            .select(
                SelectResult.expression(Meta.id.from("entries")).`as`("entryId"),
                SelectResult.all().from("entries"),
                SelectResult.all().from("extraData")
            )
            .from(DataSource.collection(Collection_Entires).`as`("entries"))
            .join(
                Join.innerJoin(DataSource.collection(Collection_ExtraData).`as`("extraData"))
                    .on(
                        Meta.id.from("entries")
                            .equalTo(Expression.property("entry_id").from("extraData"))
                    )
            )
            .where(
                Expression.property("reminder_type")
                .from("extraData")
                .isNotValued()
                .and(
                    Expression.property("repeat")
                        .from("extraData")
                        .isValued()
                )
            )
            .orderBy(
                Ordering.expression(
                    Expression.property("date").from("entries")
                ).ascending(),

                Ordering.expression(
                    Expression.property("time_minutes").from("entries")
                ).ascending(),

                Ordering.expression(
                    Function.length(Meta.id.from("entries"))
                ).ascending(),

                Ordering.expression(
                    Meta.id.from("entries")
                ).ascending()
            )
            .limit(
                Expression.intValue(limit),
                Expression.intValue(offset)
            )


        return query.execute().use { results ->
            results.map { result -> result.toMap()}
        }
    }




    // FIND all repeat types and count them
    fun countDocumentsByRepeatType(): List<Map<String, Any?>> {

        val query = QueryBuilder
        .select(
            SelectResult.property("repeat"),
            SelectResult.expression(
                Function.count(
                    Expression.property("repeat"))).`as`("repeatCount")
        )
            .from(DataSource.collection(Collection_ExtraData).`as`("extraData"))
            .where(Expression.property("repeat").isValued())
            .groupBy(Expression.property("repeat"))
            .orderBy(Ordering.property("repeat").ascending())


        return query.execute().use { results ->
            results.map { result -> result.toMap()}
        }
    }





    // Earliest and latest document time among documents in date range with specific reminder
    fun findEarliestDocumentsInRangeWithReminder(
        startDate: String,
        endDate: String,
        specificReminder: String
    ): Map<String, Any?>? {

        val query = QueryBuilder
            .select(

                SelectResult.expression(
                    Function.min(
                        Expression.property("time_minutes")
                            .from("entries"))).`as`("minTime"),

                        SelectResult.expression(
                        Function.max(
                            Expression.property("time_minutes")
                                .from("entries"))).`as`("maxTime")
            )
            .from(DataSource.collection(Collection_Entires).`as`("entries"))
            .join(
                Join.innerJoin(DataSource.collection(Collection_ExtraData).`as`("extraData"))
                    .on(
                        Meta.id.from("entries")
                            .equalTo(Expression.property("entry_id").from("extraData"))
                    )
            )
            .where(Expression.property("date")
                .from("entries")
                .between(
                    Expression.string(startDate),
                    Expression.string(endDate)
                )
                .and(
                    Expression.property("reminder_type")
                        .from("extraData")
                        .equalTo(Expression.string(specificReminder))
                )
            )


        return query.execute().use { results ->
            results.next()?.toMap()
        }
    }



    // Find all documents whose title contains a specific text fragment
    // and whose event time is later then a specific time

    fun findDocTitleLikeTextTimeLater(
        textFragment: String,
        timeFloor: Int
    ): List<Map<String, Any?>> {

        val query = QueryBuilder
        .select(
            SelectResult.expression(Meta.id.from("entries")).`as`("entryId"),
            SelectResult.all().from("entries"),
            SelectResult.all().from("extraData")
        )
            .from(DataSource.collection(Collection_Entires).`as`("entries"))
            .join(
                Join.leftJoin(DataSource.collection(Collection_ExtraData).`as`("extraData"))
                    .on(
                        Meta.id.from("entries")
                            .equalTo(Expression.property("entry_id").from("extraData"))
                    )
            )
            .where(
                Function.contains(
                    Expression.property("entry").from("entries"),
                    Expression.string(textFragment)
                )
                    .and(
                        Expression.property("time_minutes").from("entries")
                            .greaterThanOrEqualTo(Expression.intValue(timeFloor))
                    )
            )

        return query.execute().use { results ->
            results.map { result -> result.toMap()}
        }
    }









}