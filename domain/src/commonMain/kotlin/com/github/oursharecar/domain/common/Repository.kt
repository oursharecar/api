package com.github.oursharecar.domain.common

import kotlinx.coroutines.flow.Flow

data class PageRequest(val page: Int, val size: Int) {
    init {
        require(page > 0) { "page must be greater than 0" }
        require(size > 0) { "size must be greater than 0" }
    }
}

data class Page<T>(
    val items: List<T>,
    val total: Long,
    val page: Int,
    val size: Int
)

interface Repository<ID, E> {
    suspend fun findById(id: ID): E?
    suspend fun existsById(id: ID): Boolean
    suspend fun insert(entity: E): ID
    suspend fun upsert(id: ID, entity: E): Boolean
    suspend fun deleteById(id: ID): Boolean
    fun findAll(): Flow<E>
}

interface PagedRepository<ID, E> : Repository<ID, E> {
    suspend fun page(request: PageRequest): Page<E>
}
