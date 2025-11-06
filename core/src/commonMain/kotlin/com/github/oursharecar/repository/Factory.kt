package com.github.oursharecar.repository

import com.github.oursharecar.models.GroupResource

interface RepositoryFactory {
    fun getGroupRepository(): Repository<GroupResource>
}