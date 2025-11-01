package com.github.oursharecar.repository

import com.github.oursharecar.models.Group

interface RepositoryFactory {
    fun getGroupRepository(): Repository<Group>
}