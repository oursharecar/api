package com.github.oursharecar.mongo.repository

import com.github.oursharecar.domain.common.Page
import com.github.oursharecar.domain.common.PageRequest
import com.github.oursharecar.domain.common.PagedRepository
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import org.bson.conversions.Bson
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
abstract class MongoRepository<ID, E : Any>(
    protected val collection: MongoCollection<E>,
    protected val idSelector: (E) -> ID
) : PagedRepository<ID, E> {
    protected open fun baseFilter(): Bson = Filters.eq("audit.deletedAt", null)

    override suspend fun findById(id: ID): E? {
        return collection.find(Filters.and(baseFilter(), Filters.eq("_id", id))).limit(1).first()
    }

    override suspend fun existsById(id: ID): Boolean {
        return findById(id) != null
    }

    override suspend fun insert(entity: E): ID {
        collection.insertOne(entity)
        return idSelector(entity)
    }

    override suspend fun upsert(id: ID, entity: E): Boolean {
        val result = collection.replaceOne(Filters.and(baseFilter(), Filters.eq("_id", id)), entity)
        return result.matchedCount > 0 || result.upsertedId != null
    }

    override suspend fun deleteById(id: ID): Boolean {
        val now = Clock.System.now()
        val result = collection.updateOne(
            Filters.and(baseFilter(), Filters.eq("_id", id)),
            Updates.set("audit.deletedAt", now)
        )
        return result.matchedCount > 0
    }

    override fun findAll(): Flow<E> = collection.find(baseFilter())

    override suspend fun page(request: PageRequest): Page<E> {
        val filter = baseFilter()
        val total = collection.countDocuments(filter)
        val items = collection.find(filter)
            .sort(Sorts.descending("audit.createdAt"))
            .skip((request.page - 1) * request.size)
            .limit(request.size)
            .toList()
        return Page(items, total, request.page, request.size)
    }
}
