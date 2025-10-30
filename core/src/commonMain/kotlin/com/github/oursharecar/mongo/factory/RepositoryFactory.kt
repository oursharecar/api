package com.github.oursharecar.mongo.factory

import com.github.oursharecar.domain.group.GroupRepository
import com.github.oursharecar.mongo.group.MongoGroup

interface RepositoryFactory {
    fun createGroupRepository(): GroupRepository<MongoGroup>
}
