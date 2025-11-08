package com.github.oursharecar.server

import com.github.oursharecar.models.GroupResource
import com.github.oursharecar.models.ID
import com.github.oursharecar.models.Identifiable
import com.github.oursharecar.models.UserResource
import com.github.oursharecar.repository.Repository
import com.github.oursharecar.repository.RepositoryFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class LinkedMapRepository<E> : Repository<E> where E : Any, E : Identifiable<E> {
    private val storage = linkedMapOf<ID<E>, E>()
    private var nextId = 1

    fun seed(idValue: String, entity: E): ID<E> {
        val id = ID<E>(idValue)
        storage[id] = entity.withId(id)
        updateCounter(idValue)
        return id
    }

    override suspend fun findById(id: ID<E>): E? = storage[id]

    override suspend fun existsById(id: ID<E>): Boolean = storage.containsKey(id)

    override suspend fun insert(entity: E): ID<E> {
        var id: ID<E>
        do {
            val idValue = "group-${nextId++}"
            id = ID(idValue)
        } while (storage.containsKey(id))

        storage[id] = entity.withId(id)
        return id
    }

    override suspend fun deleteById(id: ID<E>): Boolean = storage.remove(id) != null

    override fun findAll(): Flow<E> = flow {
        storage.values.forEach { emit(it) }
    }

    private fun updateCounter(idValue: String) {
        val numericSuffix = idValue.substringAfterLast('-', "")
        val candidate = numericSuffix.toIntOrNull()?.plus(1) ?: return
        if (candidate > nextId) {
            nextId = candidate
        }
    }
}

internal class LinkedMapRepositoryFactory : RepositoryFactory {
    val users = LinkedMapRepository<UserResource>()
    val groups = LinkedMapRepository<GroupResource>()

    override fun getGroupRepository() = groups
    override fun getUserRepository() = users
}