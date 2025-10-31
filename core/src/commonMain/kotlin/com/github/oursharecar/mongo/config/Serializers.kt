package com.github.oursharecar.mongo.config

import com.github.oursharecar.domain.group.Group
import com.github.oursharecar.mongo.group.MongoGroup
import com.github.oursharecar.mongo.repository.MongoModel
import com.mongodb.MongoClientSettings
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.kotlinx.KotlinSerializerCodecProvider

internal val PolymorphicCodec: CodecProvider = KotlinSerializerCodecProvider(SerializersModule {
    polymorphic(Group::class) {
        subclass(MongoGroup::class)
    }
    polymorphic(MongoModel::class) {
        subclass(MongoGroup::class)
    }
})

internal val CodecRegistry: CodecRegistry = CodecRegistries.fromRegistries(
    MongoClientSettings.getDefaultCodecRegistry(),
    CodecRegistries.fromProviders(KotlinInstantCodec, PolymorphicCodec)
)