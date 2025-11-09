package com.github.oursharecar.server.service

import com.github.oursharecar.models.GroupResource
import com.github.oursharecar.models.ID
import com.github.oursharecar.models.UserResource
import com.github.oursharecar.server.models.*

interface ServerService {
    suspend fun getUser(id: ID<UserResource>): UserResource?
    suspend fun listUsers(): List<UserResource>
    suspend fun createUser(request: UserCreateRequest): ID<UserResource>
    suspend fun updateUser(request: UserUpdateRequest): Boolean
    suspend fun deleteUser(id: ID<UserResource>): Boolean

    suspend fun getGroup(id: ID<GroupResource>): GroupResource?
    suspend fun listGroups(): List<GroupResource>
    suspend fun createGroup(request: GroupCreateRequest): ID<GroupResource>
    suspend fun updateGroup(request: GroupUpdateRequest): Boolean
    suspend fun deleteGroup(id: ID<GroupResource>): Boolean

    suspend fun addGroupMember(request: GroupMemberAddRequest): Boolean
    suspend fun removeGroupMember(request: GroupMemberRemoveRequest): Boolean
}

