package com.github.oursharecar

import com.mongodb.kotlin.client.coroutine.MongoClient

class MongoRepositoryFactory(private val config: MongoRepositoryConfig) : RepositoryFactory {
    private val client: MongoClient = MongoClient.create(config.toClientSettings())
    private val database = client.getDatabase(config.databaseName)

    override fun createGroupRepository(): GroupRepository {
        return GroupMongoRepository(database, config.groupCollectionName)
    }
}
