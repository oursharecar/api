package com.github.oursharecar.mongo.factory

import com.github.oursharecar.domain.group.GroupRepository
import com.github.oursharecar.mongo.oldGroup.MongoGroup

interface RepositoryFactory {
    fun createGroupRepository(): GroupRepository<MongoGroup>
}
