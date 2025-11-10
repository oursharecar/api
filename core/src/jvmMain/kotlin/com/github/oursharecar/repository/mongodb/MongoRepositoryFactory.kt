package com.github.oursharecar.repository.mongodb

import com.github.oursharecar.models.GroupResource
import com.github.oursharecar.models.UserResource
import com.github.oursharecar.repository.Repository
import com.github.oursharecar.repository.RepositoryFactory
import com.github.oursharecar.repository.mongodb.documents.GroupDocument
import com.github.oursharecar.repository.mongodb.documents.UserDocument
import com.mongodb.kotlin.client.coroutine.MongoClient

class MongoRepositoryFactory(config: MongoRepositoryConfig) : RepositoryFactory {
    private val client = MongoClient.Factory.create(config.buildSettings())
    private val database = client.getDatabase(config.databaseName)
    private val groupCollection = database.getCollection<GroupDocument>(config.groupCollectionName)
    private val userCollection = database.getCollection<UserDocument>(config.userCollectionName)

    override fun getGroupRepository(): Repository<GroupResource> {
        return GroupRepository(groupCollection)
    }

    override fun getUserRepository(): Repository<UserResource> {
        return UserRepository(userCollection)
    }
}
