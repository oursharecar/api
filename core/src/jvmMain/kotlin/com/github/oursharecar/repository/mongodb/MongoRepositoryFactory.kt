package com.github.oursharecar.repository.mongodb

import com.github.oursharecar.models.GroupResource
import com.github.oursharecar.repository.Repository
import com.github.oursharecar.repository.RepositoryFactory
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.github.oursharecar.repository.mongodb.documents.Group as GroupDocument

class MongoRepositoryFactory(config: MongoRepositoryConfig) : RepositoryFactory {
    private val client = MongoClient.Factory.create(config.clientSettings)
    private val database = client.getDatabase(config.databaseName)
    private val groupCollection = database.getCollection<GroupDocument>(config.groupCollectionName)

    override fun getGroupRepository(): Repository<GroupResource> {
        return GroupRepository(groupCollection)
    }
}
