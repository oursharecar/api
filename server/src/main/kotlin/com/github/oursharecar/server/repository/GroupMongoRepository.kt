package com.github.oursharecar.server.repository

import com.github.oursharecar.server.models.GroupResource
import com.github.oursharecar.server.models.ID
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.bson.conversions.Bson
import org.bson.types.ObjectId

object IdConverter {
    fun convert(id: ID<*>): ObjectId = ObjectId(id.id)
    fun <T> convert(id: ObjectId): ID<T> = ID(id.toHexString())
}

class GroupMongoRepository(val collection: MongoCollection<GroupMongoDocument>) : GroupRepository {
    override suspend fun findById(id: ID<GroupResource>): GroupResource? {
        return collection.findById(IdConverter.convert(id))?.toGroupResource()
    }

    override suspend fun existsById(id: ID<GroupResource>): Boolean {
        return this.findById(id) != null
    }

    override suspend fun findBySlug(slug: String): GroupResource? {
        TODO("Not yet implemented")
    }

    override suspend fun existsBySlug(slug: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun insert(entity: GroupResource): ID<GroupResource> {
        TODO("Not yet implemented")
    }

    override suspend fun upsert(
        id: ID<GroupResource>,
        entity: GroupResource
    ): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteById(id: ID<GroupResource>): Boolean {
        TODO("Not yet implemented")
    }

    override fun findAll(): Flow<GroupResource> {
        return collection.find().map { it.toGroupResource() }
    }
}

private suspend fun <T : Any> MongoCollection<T>.findById(id: ObjectId, vararg filters: Bson): T? {
    return this.find(Filters.and(*filters, Filters.eq("_id", id)))
        .limit(1)
        .firstOrNull()
}