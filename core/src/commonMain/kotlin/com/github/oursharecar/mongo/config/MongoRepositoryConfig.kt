package com.github.oursharecar.mongo.config

import com.github.oursharecar.domain.common.ID
import com.github.oursharecar.domain.group.Group
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry

interface MongoRepositoryConfig {
    val connectionString: String?
    val databaseName: String
    val groupCollectionName: String
}

fun MongoRepositoryConfig.toClientSettings(): MongoClientSettings {
    val builder = MongoClientSettings.builder()
    connectionString?.let { builder.applyConnectionString(ConnectionString(it)) }
    builder.codecRegistry(mongoCodecRegistry())
    return builder.build()
}

private val mongoCodecRegistryInstance: CodecRegistry =
    CodecRegistries.fromRegistries(
        MongoClientSettings.getDefaultCodecRegistry(),
        CodecRegistries.fromProviders(KotlinInstantCodec, IDCodec<Group>(ID::class.java)),
    )

internal fun mongoCodecRegistry(): CodecRegistry = mongoCodecRegistryInstance
