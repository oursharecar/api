package com.github.oursharecar.mongo.group

import com.github.oursharecar.domain.common.Page
import com.github.oursharecar.domain.common.PageRequest
import com.github.oursharecar.domain.group.Group
import com.github.oursharecar.domain.group.GroupRepository
import com.github.oursharecar.mongo.repository.MongoCollectionRepository
import com.github.oursharecar.mongo.repository.MongoRepository
import com.mongodb.kotlin.client.coroutine.MongoCollection

class MongoGroupRepository(override val repo: MongoCollectionRepository<MongoGroup>) :
    MongoRepository<Group, MongoGroup>({ MongoGroup(it) }),
    GroupRepository<Group> {

    constructor(collection: MongoCollection<MongoGroup>) : this(MongoCollectionRepository(collection))

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