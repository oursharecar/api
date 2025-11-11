package com.github.oursharecar.repository.mongodb

import com.github.oursharecar.models.ID
import com.github.oursharecar.repository.*
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
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
    override fun findAll(limit: Int) = collection.find().limit(limit)
    override suspend fun findAll(pageRequest: PageRequest): Page<T> {
        var findFlow = collection.find()
        if (pageRequest.sort.isSorted) {
            val sortDefinitions = pageRequest.sort.fields.toBson()
            if (sortDefinitions.isNotEmpty()) {
                findFlow = findFlow.sort(Sorts.orderBy(sortDefinitions))
            }
        }

        val content = findFlow
            .skip(pageRequest.offset.toInt())
            .limit(pageRequest.size)
            .toList()

        val total = collection.countDocuments()

        return Page(
            content = content,
            page = pageRequest.page,
            size = pageRequest.size,
            totalElements = total,
            sort = pageRequest.sort
        )
    }
}

private fun List<SortField>.toBson() = map { field ->
    when (field.direction) {
        SortDirection.ASC -> Sorts.ascending(field.property)
        SortDirection.DESC -> Sorts.descending(field.property)
    }
}
