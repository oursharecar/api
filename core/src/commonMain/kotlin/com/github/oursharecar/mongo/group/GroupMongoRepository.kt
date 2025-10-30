package com.github.oursharecar.mongo.group

import com.github.oursharecar.domain.group.GroupRepository
import com.github.oursharecar.mongo.repository.MongoRepository
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull

class GroupMongoRepository(
    database: MongoDatabase,
    collectionName: String
) : MongoRepository<MongoGroup>(
    database.getCollection<MongoGroup>(collectionName),
), GroupRepository<MongoGroup> {
    override suspend fun findBySlug(slug: String): MongoGroup? {
        return collection
            .find(Filters.and(baseFilter(), Filters.eq("slug", slug)))
            .firstOrNull()
    }

    override suspend fun existsBySlug(slug: String): Boolean {
        return findBySlug(slug) != null
    }
}
