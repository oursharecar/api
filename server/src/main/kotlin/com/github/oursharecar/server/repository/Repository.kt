package com.github.oursharecar.server.repository

import com.github.oursharecar.server.models.ID
import kotlinx.coroutines.flow.Flow

interface Repository<T : Any> {
    suspend fun findById(id: ID<T>): T?
    suspend fun existsById(id: ID<T>): Boolean = findById(id) != null
    suspend fun findBySlug(slug: String): T?
    suspend fun existsBySlug(slug: String): Boolean = findBySlug(slug) != null
    suspend fun insert(entity: T): ID<T>
    suspend fun deleteById(id: ID<T>): Boolean
    fun findAll(): Flow<T>
}