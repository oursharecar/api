package com.github.oursharecar.mongo.oldGroup

import com.github.oursharecar.domain.group.GroupRepository
import com.github.oursharecar.mongo.repository.MMongoRepository
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull

class GroupMongoRepository(
    database: MongoDatabase,
    collectionName: String
) : MMongoRepository<MongoGroup>(
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
