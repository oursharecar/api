package com.github.oursharecar.mongo.config

import com.github.oursharecar.domain.common.DomainSerializersModule
import com.github.oursharecar.domain.common.ID
import com.github.oursharecar.domain.group.Group
import com.github.oursharecar.mongo.group.MongoGroup
import com.github.oursharecar.mongo.repository.MongoModel
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.kotlinx.BsonConfiguration
import org.bson.codecs.kotlinx.KotlinSerializerCodecProvider

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

@Suppress("UNCHECKED_CAST")
private val idCodec: IDCodec<Any?> = IDCodec(ID::class.java as Class<ID<*>>)

val MongoSerializersModule: SerializersModule = SerializersModule {
    polymorphic(Group::class) {
        subclass(MongoGroup::class)
    }
    polymorphic(MongoModel::class) {
        subclass(MongoGroup::class)
    }
}

private val mongoCodecRegistryInstance: CodecRegistry =
    CodecRegistries.fromRegistries(
        MongoClientSettings.getDefaultCodecRegistry(),
        CodecRegistries.fromProviders(
            KotlinInstantCodec,
            idCodec,
            KotlinSerializerCodecProvider(MongoSerializersModule, BsonConfiguration()),
            KotlinSerializerCodecProvider(DomainSerializersModule, BsonConfiguration())
        ),
    )

internal fun mongoCodecRegistry(): CodecRegistry = mongoCodecRegistryInstance
