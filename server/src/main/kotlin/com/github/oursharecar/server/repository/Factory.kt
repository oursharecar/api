package com.github.oursharecar.server.repository

import com.github.oursharecar.server.models.GroupResource

interface RepositoryFactory {
    fun getGroupRepository(): Repository<GroupResource>
}