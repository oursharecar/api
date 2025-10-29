package com.github.oursharecar.mongo.config

import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistry
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
object KotlinInstantCodec : Codec<Instant>, CodecProvider {
    override fun encode(writer: BsonWriter, value: Instant, encoderContext: EncoderContext) {
        writer.writeDateTime(value.toEpochMilliseconds())
    }

    override fun decode(reader: BsonReader, decoderContext: DecoderContext): Instant {
        return Instant.fromEpochMilliseconds(reader.readDateTime())
    }

    override fun getEncoderClass(): Class<Instant> = Instant::class.java

    override fun <T : Any?> get(clazz: Class<T>, registry: CodecRegistry): Codec<T>? {
        @Suppress("UNCHECKED_CAST")
        return if (clazz == Instant::class.java) this as Codec<T> else null
    }
}

