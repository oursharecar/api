package com.github.oursharecar

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull

class GroupMongoRepository(
    database: MongoDatabase,
    collectionName: String
) : MongoRepository<String, Group>(database.getCollection(collectionName), { it.id }), GroupRepository {

    override suspend fun findBySlug(slug: String): Group? {
        return collection
            .find(Filters.and(baseFilter(), Filters.eq("slug", slug)))
            .firstOrNull()
    }

    override suspend fun existsBySlug(slug: String): Boolean {
        return findBySlug(slug) != null
    }
}
