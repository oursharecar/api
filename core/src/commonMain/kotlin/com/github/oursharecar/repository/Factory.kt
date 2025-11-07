package com.github.oursharecar.repository

import com.github.oursharecar.models.GroupResource
import com.github.oursharecar.models.UserResource

interface RepositoryFactory {
    fun getGroupRepository(): Repository<GroupResource>
    fun getUserRepository(): Repository<UserResource>
}