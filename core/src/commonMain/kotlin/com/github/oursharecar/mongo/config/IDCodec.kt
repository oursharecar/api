package com.github.oursharecar.mongo.config

import com.github.oursharecar.domain.common.ID
import org.bson.BsonInvalidOperationException
import org.bson.BsonReader
import org.bson.BsonType
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistry

class IDCodec<E>(private val clazz: Class<ID<*>>) : Codec<ID<*>>, CodecProvider {
    override fun encode(writer: BsonWriter, value: ID<*>, encoderContext: EncoderContext) {
        writer.writeString(value.id)
    }

    override fun decode(reader: BsonReader, decoderContext: DecoderContext): ID<E> {
        return try {
            ID(reader.readString())
        } catch (unexpected: BsonInvalidOperationException) {
            if (reader.currentBsonType == BsonType.OBJECT_ID) {
                ID(reader.readObjectId().toHexString())
            } else {
                throw unexpected
            }
        }
    }

    override fun getEncoderClass(): Class<ID<*>> = clazz

    override fun <T : Any?> get(clazz: Class<T>, registry: CodecRegistry): Codec<T>? {
        @Suppress("UNCHECKED_CAST")
        return if (clazz == this.clazz) this as Codec<T> else null
    }
}
