package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.DAO

import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.json_Operations.GeneratedEvent
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.database_setup.CouchbaseLiteProvider
import com.couchbase.lite.DataSource
import com.couchbase.lite.Expression
import com.couchbase.lite.Join
import com.couchbase.lite.Meta
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import com.couchbase.lite.UnitOfWork

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






}