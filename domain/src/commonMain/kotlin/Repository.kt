package com.github.oursharecar

import kotlinx.coroutines.flow.Flow

data class PageRequest(val page: Int, val size: Int)
data class Page<T>(val items: List<T>, val total: Long, val page: Int, val size: Int)

interface Repository<ID, E> {
    suspend fun findById(id: ID): E?
    suspend fun existsById(id: ID): Boolean
    suspend fun insert(entity: E): ID
    suspend fun upsert(id: ID, entity: E): Boolean
    suspend fun deleteById(id: ID): Boolean
    fun findAll(): Flow<E>
}

interface PagedRepository<ID, E> : Repository<ID, E> {
    suspend fun page(req: PageRequest): Page<E>
}