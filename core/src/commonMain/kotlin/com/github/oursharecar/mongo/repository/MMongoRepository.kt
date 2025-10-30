package com.github.oursharecar.mongo.repository

import com.github.oursharecar.domain.common.ID
import com.github.oursharecar.domain.common.Page
import com.github.oursharecar.domain.common.PageRequest
import com.github.oursharecar.domain.common.PagedRepository
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.BsonInvalidOperationException
import org.bson.BsonType
import org.bson.BsonValue
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
abstract class MMongoRepository<E : Any>(
    protected val collection: MongoCollection<E>,
) : PagedRepository<E> {
    protected open fun baseFilter(): Bson = Filters.eq("audit.deleted_at", null)

    override suspend fun findById(id: ID<E>): E? {
        return collection
            .find(Filters.and(baseFilter(), idFilter(id)))
            .limit(1)
            .firstOrNull()
    }

    override suspend fun existsById(id: ID<E>): Boolean {
        return findById(id) != null
    }

    override suspend fun insert(entity: E): ID<E> {
        val result = collection.insertOne(entity)
        val insertedId = result.insertedId ?: throw IllegalStateException("inserted document is missing _id")
        return ID(insertedId.asStringId())
    }

    override suspend fun upsert(id: ID<E>, entity: E): Boolean {
        val result = collection.replaceOne(Filters.and(baseFilter(), idFilter(id)), entity)
        return result.matchedCount > 0 || result.upsertedId != null
    }

    override suspend fun deleteById(id: ID<E>): Boolean {
        val now = Clock.System.now()
        val result = collection.updateOne(
            Filters.and(baseFilter(), idFilter(id)),
            Updates.set("audit.deleted_at", now)
        )
        return result.matchedCount > 0
    }

    override fun findAll(): Flow<E> = collection.find(baseFilter())

    override suspend fun page(request: PageRequest): Page<E> {
        val filter = baseFilter()
        val total = collection.countDocuments(filter)
        val items = collection.find(filter)
            .sort(Sorts.descending("audit.created_at"))
            .skip((request.page - 1) * request.size)
            .limit(request.size)
            .toList()
        return Page(items, total, request.page, request.size)
    }

    private fun idFilter(id: ID<E>): Bson {
        val filters = mutableListOf<Bson>(Filters.eq("_id", id.id))
        if (ObjectId.isValid(id.id)) {
            filters += Filters.eq("_id", ObjectId(id.id))
        }
        return if (filters.size == 1) filters.first() else Filters.or(filters)
    }

    private fun BsonValue.asStringId(): String = when (bsonType) {
        BsonType.STRING -> asString().value
        BsonType.OBJECT_ID -> asObjectId().value.toHexString()
        else -> throw BsonInvalidOperationException("Unsupported _id type: $bsonType")
    }
}
