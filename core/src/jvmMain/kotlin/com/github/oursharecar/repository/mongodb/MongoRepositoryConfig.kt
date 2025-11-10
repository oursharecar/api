package com.github.oursharecar.repository.mongodb

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import org.bson.codecs.configuration.CodecRegistries

interface MongoRepositoryConfig {
    val connectionString: String
    val databaseName: String
    val groupCollectionName: String
    val userCollectionName: String
}

internal fun MongoRepositoryConfig.buildSettings(): MongoClientSettings {
    return MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(this.connectionString))
        .codecRegistry(
            CodecRegistries.fromRegistries(
                CodecRegistries.fromCodecs(InstantCodec),
                MongoClientSettings.getDefaultCodecRegistry()
            )
        )
        .build()
}