package com.github.oursharecar.server.repository

import com.github.oursharecar.server.models.GroupResource
import com.github.oursharecar.server.models.ID
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.Flow

object IdConverter {
    fun convert(id: ID<*>): ObjectId = ObjectId(id.id)
    fun <T> convert(id: ObjectId): ID<T> = ID(id.toHexString())
}

class GroupMongoRepository(val collection: MongoCollection<GroupMongoDocument>) : GroupRepository {
    override suspend fun findById(id: ID<GroupResource>): GroupResource? {
        TODO("Not yet implemented")
    }

    override suspend fun existsById(id: ID<GroupResource>): Boolean {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

}