package com.github.oursharecar.repository.mongodb

import com.mongodb.MongoClientSettings

interface MongoRepositoryConfig {
    val clientSettings: MongoClientSettings
    val databaseName: String
    val groupCollectionName: String
    val userCollectionName: String
}
