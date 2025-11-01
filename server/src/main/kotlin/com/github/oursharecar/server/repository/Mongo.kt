package com.github.oursharecar.server.repository

import com.github.oursharecar.server.models.ID
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.bson.conversions.Bson
import org.bson.types.ObjectId

class MongoRepository<T : Any>(
    private val collection: MongoCollection<T>,
) : Repository<T> {
    private suspend fun findFirst(filter: Bson): T? {
        val document: T? = collection.find(filter).limit(1).firstOrNull()
        return document
    }

    override suspend fun findById(id: ID<T>): T? {
        val filter: Bson = Filters.eq("_id", ObjectId(id.id))
        return findFirst(filter)
    }

    override suspend fun findBySlug(slug: String): T? {
        val filter = Filters.eq("slug", slug)
        return findFirst(filter)
    }

    override suspend fun insert(entity: T): ID<T> {
        val result = collection.insertOne(entity)
        val insertedId =
            result.insertedId?.asObjectId()?.value ?: throw IllegalStateException("Failed to retrieve inserted ID")
        return ID(insertedId.toHexString())
    }

    override suspend fun deleteById(id: ID<T>): Boolean {
        val filter: Bson = Filters.eq("_id", ObjectId(id.id))
        val result = collection.deleteOne(filter)
        return result.deletedCount > 0
    }

    override fun findAll(): Flow<T> = collection.find()
}

interface WrappedRepository<R : Any, T : Any> : Repository<R> {
    val impl: Repository<T>

    fun T.toResource(): R

    fun R.toDocument(): T

    fun ID<T>.toResourceId(): ID<R>

    fun ID<R>.toDocumentId(): ID<T>

    override suspend fun findById(id: ID<R>): R? = impl.findById(id.toDocumentId())?.toResource()
    override suspend fun findBySlug(slug: String): R? = impl.findBySlug(slug)?.toResource()
    override suspend fun insert(entity: R): ID<R> {
        val documentId = impl.insert(entity.toDocument())
        return documentId.toResourceId()
    }

    override suspend fun deleteById(id: ID<R>): Boolean = impl.deleteById(id.toDocumentId())
    override fun findAll(): Flow<R> = impl.findAll().map { it.toResource() }
}

