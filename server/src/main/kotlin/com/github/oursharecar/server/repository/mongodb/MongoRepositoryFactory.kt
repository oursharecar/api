package com.github.oursharecar.server.repository.mongodb

import com.github.oursharecar.server.config.MongoConfig
import com.github.oursharecar.server.models.GroupResource
import com.github.oursharecar.server.repository.Repository
import com.github.oursharecar.server.repository.RepositoryFactory
import com.github.oursharecar.server.repository.mongodb.documents.GroupMongoDocument
import com.mongodb.kotlin.client.coroutine.MongoClient

class MongoRepositoryFactory(config: MongoConfig) : RepositoryFactory {
    private val client = MongoClient.Factory.create(config.clientSettings)
    private val database = client.getDatabase(config.databaseName)
    private val groupCollection = database.getCollection<GroupMongoDocument>(config.groupCollectionName)

    override fun getGroupRepository(): Repository<GroupResource> {
        return GroupRepository(groupCollection)
    }
}