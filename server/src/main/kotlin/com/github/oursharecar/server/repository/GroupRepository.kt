package com.github.oursharecar.server.repository

import com.github.oursharecar.server.models.GroupResource
import com.github.oursharecar.server.models.ID
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    suspend fun findById(id: ID<GroupResource>): GroupResource?
    suspend fun existsById(id: ID<GroupResource>): Boolean
    suspend fun findBySlug(slug: String): GroupResource?
    suspend fun existsBySlug(slug: String): Boolean
    suspend fun insert(entity: GroupResource): ID<GroupResource>
    suspend fun upsert(id: ID<GroupResource>, entity: GroupResource): Boolean
    suspend fun deleteById(id: ID<GroupResource>): Boolean
    fun findAll(): Flow<GroupResource>
}