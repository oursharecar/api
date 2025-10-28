package com.github.oursharecar

interface RepositoryFactory {
    fun createGroupRepository(): GroupRepository
}

