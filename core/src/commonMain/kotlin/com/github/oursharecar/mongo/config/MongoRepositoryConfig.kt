package com.github.oursharecar.mongo.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings

interface MongoRepositoryConfig {
    val connectionString: String?
    val databaseName: String
    val groupCollectionName: String
}

fun MongoRepositoryConfig.toClientSettings(): MongoClientSettings {
    val builder = MongoClientSettings.builder()
    connectionString?.let { builder.applyConnectionString(ConnectionString(it)) }
    return builder.build()
}
