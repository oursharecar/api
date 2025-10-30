package com.github.oursharecar.mongo.repository

import com.github.oursharecar.domain.common.ID
import com.github.oursharecar.domain.common.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

interface MongoRepository<E, M : MongoModel<E>> : Repository<E> {
    val repo: MongoCollectionRepository<M>

    override suspend fun findById(id: ID<E>): E? {
        return repo.findAll().first().withoutId()
    }

    override suspend fun existsById(id: ID<E>): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun insert(entity: E): ID<E> {
        TODO("Not yet implemented")
    }

    override suspend fun upsert(id: ID<E>, entity: E): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteById(id: ID<E>): Boolean {
        TODO("Not yet implemented")
    }

    override fun findAll(): Flow<E> {
        return repo.findAll().map { it.withoutId() }
    }
}