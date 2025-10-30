package com.github.oursharecar.mongo.factory

import com.github.oursharecar.domain.group.GroupRepository
import com.github.oursharecar.mongo.config.MongoRepositoryConfig
import com.github.oursharecar.mongo.config.toClientSettings
import com.github.oursharecar.mongo.oldGroup.GroupMongoRepository
import com.github.oursharecar.mongo.oldGroup.MongoGroup
import com.mongodb.kotlin.client.coroutine.MongoClient

class MongoRepositoryFactory(private val config: MongoRepositoryConfig) : RepositoryFactory {
    private val client: MongoClient = MongoClient.create(config.toClientSettings())
    private val database = client.getDatabase(config.databaseName)

    override fun createGroupRepository(): GroupRepository<MongoGroup> {
        return GroupMongoRepository(database, config.groupCollectionName)
    }
}
