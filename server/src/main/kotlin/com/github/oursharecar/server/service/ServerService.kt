package com.github.oursharecar.server.service

import com.github.oursharecar.models.GroupResource
import com.github.oursharecar.models.ID
import com.github.oursharecar.models.UserResource
import com.github.oursharecar.repository.Repository
import com.github.oursharecar.repository.RepositoryFactory
import com.github.oursharecar.server.models.*
import com.github.oursharecar.server.utils.GlobalSlugify
import kotlinx.coroutines.flow.toList

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

class ServerServiceImpl(factory: RepositoryFactory) : ServerService {
    private val groups: Repository<GroupResource> = factory.getGroupRepository()

    override suspend fun getUser(id: ID<UserResource>): UserResource? {
        TODO("Not yet implemented")
    }

    override suspend fun listUsers(): List<UserResource> {
        TODO("Not yet implemented")
    }

    override suspend fun createUser(request: UserCreateRequest): ID<UserResource> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(request: UserUpdateRequest): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(id: ID<UserResource>): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getGroup(id: ID<GroupResource>): GroupResource? {
        return groups.findById(id)
    }

    override suspend fun listGroups(): List<GroupResource> {
        return groups.findAll().toList()
    }

    override suspend fun createGroup(request: GroupCreateRequest): ID<GroupResource> {
        val sub = "sub(placeholder)"
        val slug = GlobalSlugify.slugify(request.name)
        val newResource = request.buildResource(sub, slug)
        return groups.insert(newResource)
    }

    override suspend fun updateGroup(request: GroupUpdateRequest): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteGroup(id: ID<GroupResource>): Boolean {
        return groups.deleteById(id)
    }

    override suspend fun addGroupMember(request: GroupMemberAddRequest): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun removeGroupMember(request: GroupMemberRemoveRequest): Boolean {
        TODO("Not yet implemented")
    }
}