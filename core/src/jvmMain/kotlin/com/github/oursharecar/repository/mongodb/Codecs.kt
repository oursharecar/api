package com.github.oursharecar.repository.mongodb

import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
object InstantCodec : Codec<Instant> {
    override fun encode(writer: BsonWriter, value: Instant, encoderContext: EncoderContext) =
        writer.writeDateTime(value.toEpochMilliseconds())

    override fun decode(reader: BsonReader, decoderContext: DecoderContext): Instant {
        return Instant.Companion.fromEpochMilliseconds(reader.readDateTime())
    }

    override fun getEncoderClass(): Class<Instant> = Instant::class.java
}