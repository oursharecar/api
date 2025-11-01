package com.github.oursharecar.repository.mongodb

import com.github.oursharecar.models.Group
import com.github.oursharecar.repository.Repository
import com.github.oursharecar.repository.RepositoryFactory
import com.github.oursharecar.repository.mongodb.documents.GroupMongoDocument
import com.mongodb.kotlin.client.coroutine.MongoClient

class MongoRepositoryFactory(config: MongoRepositoryConfig) : RepositoryFactory {
    private val client = MongoClient.Factory.create(config.clientSettings)
    private val database = client.getDatabase(config.databaseName)
    private val groupCollection = database.getCollection<GroupMongoDocument>(config.groupCollectionName)

    override fun getGroupRepository(): Repository<Group> {
        return GroupRepository(groupCollection)
    }
}
