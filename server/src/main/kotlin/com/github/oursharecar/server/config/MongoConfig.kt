package com.github.oursharecar.server.config

import com.mongodb.MongoClientSettings
import io.ktor.server.config.*
import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import org.bson.codecs.configuration.CodecRegistries
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

interface MongoRepositoryConfig {
    val connectionString: String?
    val databaseName: String
    val groupCollectionName: String
}

class MongoConfig(config: ApplicationConfig) : MongoRepositoryConfig {
    private val path = config.config("mongodb")

    override val connectionString: String? =
        path.propertyOrNull("connectionString")?.getString()
    override val databaseName: String =
        path.propertyOrNull("database")?.getString() ?: "oursharecar"
    override val groupCollectionName: String =
        path.propertyOrNull("collections.group")?.getString() ?: "groups"

    val clientSettings: MongoClientSettings = MongoClientSettings.builder().apply {
        connectionString?.let {
            applyConnectionString(com.mongodb.ConnectionString(it))
            codecRegistry(
                CodecRegistries.fromRegistries(
                    CodecRegistries.fromCodecs(InstantCodec),
                    MongoClientSettings.getDefaultCodecRegistry()
                )
            )
        }
    }.build()
}

@OptIn(ExperimentalTime::class)
object InstantCodec : Codec<Instant> {
    override fun encode(writer: BsonWriter, value: Instant, encoderContext: EncoderContext) =
        writer.writeDateTime(value.toEpochMilliseconds())

    override fun decode(reader: BsonReader, decoderContext: DecoderContext): Instant {
        return Instant.fromEpochMilliseconds(reader.readDateTime())
    }

    override fun getEncoderClass(): Class<Instant> = Instant::class.java
}