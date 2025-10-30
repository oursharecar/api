package com.github.oursharecar.mongo.repository

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import org.bson.conversions.Bson
import org.bson.types.ObjectId

class MongoCollectionRepository<T : HasObjectId>(
    val holder: MongoCollectionHolder<T>,
    val commonFilter: Bson = Filters.empty()
) {
    constructor(collection: MongoCollection<T>) : this(MongoCollectionHolder(collection))

    private val col = holder.col

    suspend fun findById(id: ObjectId): T? {
        return col
            .find(Filters.and(commonFilter, Filters.eq("_id", id)))
            .limit(1)
            .firstOrNull()
    }

    suspend fun existsById(id: ObjectId): Boolean = findById(id) != null

    suspend fun insert(entity: T): ObjectId {
        val result = col.insertOne(entity)
        val insertedId = result.insertedId ?: throw IllegalStateException("inserted document is missing _id")
        return insertedId.asObjectId().value
    }

    suspend fun upsert(id: ObjectId, entity: T): Boolean {
        val result = col.replaceOne(Filters.and(commonFilter, Filters.eq("_id", id)), entity)
        return result.matchedCount > 0 || result.upsertedId != null
    }

    suspend fun deleteById(id: ObjectId): Boolean {
        val result = col.deleteOne(Filters.and(commonFilter, Filters.eq("_id", id)))
        return result.deletedCount > 0
    }

    fun findAll(): Flow<T> = holder.col.find()
}