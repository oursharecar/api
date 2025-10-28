package com.github.oursharecar.mongo.factory

import com.github.oursharecar.domain.group.GroupRepository

interface RepositoryFactory {
    fun createGroupRepository(): GroupRepository
}
