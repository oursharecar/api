package com.github.oursharecar.server.config

interface MongoRepositoryConfig {
    val connectionString: String?
    val databaseName: String
    val groupCollectionName: String
}