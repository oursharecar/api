package com.github.oursharecar.mongo.factory

import com.github.oursharecar.mongo.config.MongoRepositoryConfig
import com.github.oursharecar.mongo.config.toClientSettings
import com.github.oursharecar.mongo.group.MongoGroupRepository
import com.mongodb.kotlin.client.coroutine.MongoClient

class MongoRepositoryFactory(private val config: MongoRepositoryConfig) : RepositoryFactory {
    private val client: MongoClient = MongoClient.create(config.toClientSettings())
    private val database = client.getDatabase(config.databaseName)

    override fun createGroupRepository(): MongoGroupRepository {
        return MongoGroupRepository(database.getCollection<com.github.oursharecar.mongo.group.MongoGroup>(config.groupCollectionName))
    }
}
