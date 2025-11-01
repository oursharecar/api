package com.github.oursharecar.server.repository

import com.github.oursharecar.server.config.MongoConfig
import com.github.oursharecar.server.models.GroupResource
import com.mongodb.kotlin.client.coroutine.MongoClient

interface RepositoryFactory {
    fun getGroupRepository(): Repository<GroupResource>
}

class MongoRepositoryFactory(config: MongoConfig) : RepositoryFactory {
    private val client = MongoClient.create(config.clientSettings)
    private val database = client.getDatabase(config.databaseName)
    private val groupCollection = database.getCollection<GroupMongoDocument>(config.groupCollectionName)

    override fun getGroupRepository(): Repository<GroupResource> {
        return GroupMongoRepository(groupCollection)
    }
}