package com.github.oursharecar.domain.common

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

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


@JvmInline
@Serializable
value class ID<E>(val id: String)

interface Repository<E> {
    suspend fun findById(id: ID<E>): E?
    suspend fun existsById(id: ID<E>): Boolean
    suspend fun insert(entity: E): ID<E>
    suspend fun upsert(id: ID<E>, entity: E): Boolean
    suspend fun deleteById(id: ID<E>): Boolean
    fun findAll(): Flow<E>
}

interface PagedRepository<E> : Repository<E> {
    suspend fun page(request: PageRequest): Page<E>
}
