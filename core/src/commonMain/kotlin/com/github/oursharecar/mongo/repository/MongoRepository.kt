package com.github.oursharecar.mongo.repository

import com.github.oursharecar.domain.common.ID
import com.github.oursharecar.domain.common.Repository
import com.github.oursharecar.domain.group.DomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.bson.types.ObjectId

abstract class MongoRepository<E : DomainModel, M : MongoModel<E>>(val entityCreation: (E) -> M) : Repository<E> {
    abstract val repo: MongoCollectionRepository<M>

    override suspend fun findById(id: ID<E>): E? {
        return repo.findById(id.toObjectId())?.dropId()
    }

    override suspend fun existsById(id: ID<E>): Boolean {
        return repo.existsById(id.toObjectId())
    }

    override suspend fun insert(entity: E): ID<E> {
        return repo.insert(entityCreation(entity)).toTypedId()
    }

    override suspend fun upsert(id: ID<E>, entity: E): Boolean {
        return repo.upsert(id.toObjectId(), entityCreation(entity))
    }

    override suspend fun deleteById(id: ID<E>): Boolean {
        return repo.deleteById(id.toObjectId())
    }

    override fun findAll(): Flow<E> {
        return repo.findAll().map { it.dropId() }
    }
}

private fun ID<*>.toObjectId() = ObjectId(this.id)
private fun <T> ObjectId.toTypedId() = ID<T>(this.toHexString())