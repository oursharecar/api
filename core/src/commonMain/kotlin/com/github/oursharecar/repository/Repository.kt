package com.github.oursharecar.repository

import com.github.oursharecar.models.ID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface Repository<T : Any> {
    suspend fun findById(id: ID<T>): T?
    suspend fun existsById(id: ID<T>): Boolean = findById(id) != null
    suspend fun findBySlug(slug: String): T?
    suspend fun existsBySlug(slug: String): Boolean = findBySlug(slug) != null
    suspend fun insert(entity: T): ID<T>
    suspend fun deleteById(id: ID<T>): Boolean
    fun findAll(): Flow<T>
}

interface WrappedRepository<R : Any, T : Any> : Repository<R> {
    val impl: Repository<T>

    fun T.toResource(): R

    fun R.toDocument(): T

    fun ID<T>.toResourceId(): ID<R>

    fun ID<R>.toDocumentId(): ID<T>

    override suspend fun findById(id: ID<R>): R? = impl.findById(id.toDocumentId())?.toResource()
    override suspend fun findBySlug(slug: String): R? = impl.findBySlug(slug)?.toResource()
    override suspend fun insert(entity: R): ID<R> {
        val documentId = impl.insert(entity.toDocument())
        return documentId.toResourceId()
    }

    override suspend fun deleteById(id: ID<R>): Boolean = impl.deleteById(id.toDocumentId())
    override fun findAll(): Flow<R> = impl.findAll().map { it.toResource() }
}