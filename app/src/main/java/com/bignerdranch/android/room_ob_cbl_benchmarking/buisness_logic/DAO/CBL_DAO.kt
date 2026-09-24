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


    // INSERT ENTRIES BULK
    fun insertDocuments(events: List<GeneratedEvent>) {

        val startingId = nextEntryId

        try {
            database.inBatch(UnitOfWork {
                for (event in events) {
                    insertDocument(event)
                }
            })
        } catch (error: Exception) {
            nextEntryId = startingId
            throw error
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
            .where(Expression.property("date").equalTo(Expression.string(date)))
            .orderBy(
                Ordering.property("date").ascending(),
                Ordering.property("time_minutes").ascending()
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
    fun findDocumentsWithSpecificReminder(): List<Map<String, Any?>> {

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
                .equalTo(Expression.string("10 mins before"))
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




}