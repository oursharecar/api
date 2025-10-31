package com.github.oursharecar.mongo.group

import com.github.oursharecar.domain.common.Page
import com.github.oursharecar.domain.common.PageRequest
import com.github.oursharecar.domain.common.Repository
import com.github.oursharecar.domain.group.Group
import com.github.oursharecar.domain.group.GroupRepository
import com.github.oursharecar.mongo.repository.MongoCollectionRepository
import com.github.oursharecar.mongo.repository.MongoRepository
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase

interface InternalMongoGroupRepository : Repository<Group>, GroupRepository<Group>

internal class InternalMongoRepositoryImpl(collection: MongoCollection<MongoGroup>): MongoRepository<Group, MongoGroup>, GroupRepository<Group>, InternalMongoGroupRepository {
    override val repo: MongoCollectionRepository<MongoGroup> = MongoCollectionRepository(collection)
    override val entityCreation: (Group) -> MongoGroup = { MongoGroup(it) }

    override suspend fun findBySlug(slug: String): Group? {
        TODO("Not yet implemented")
    }

    override suspend fun existsBySlug(slug: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun page(request: PageRequest): Page<Group> {
        TODO("Not yet implemented")
    }
}

class MongoGroupRepository private constructor(impl: InternalMongoGroupRepository): InternalMongoGroupRepository by impl {
    constructor(database: MongoDatabase, collectionName: String): this(
        InternalMongoRepositoryImpl(database.getCollection(collectionName))
    )
}
