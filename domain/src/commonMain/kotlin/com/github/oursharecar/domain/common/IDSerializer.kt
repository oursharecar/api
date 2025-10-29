package com.github.oursharecar.domain.common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object IDSerializer : KSerializer<ID<Any?>> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ID<Any?> {
        val decoded = tryDecodeString(decoder) ?: tryDecodeObjectId(decoder)
        ?: error("Unable to decode ID using decoder ${decoder::class.java.simpleName}")
        return ID<Any?>(decoded)
    }

    override fun serialize(encoder: Encoder, value: ID<Any?>) {
        encoder.encodeString(value.id)
    }

    private fun tryDecodeString(decoder: Decoder): String? = try {
        decoder.decodeString()
    } catch (_: Throwable) {
        null
    }

    private fun tryDecodeObjectId(decoder: Decoder): String? {
        val decodeMethod = decoder::class.java.methods.firstOrNull { method ->
            method.name == "decodeObjectId" && method.parameterCount == 0
        } ?: return null

        val objectId = runCatching { decodeMethod.invoke(decoder) }.getOrNull() ?: return null
        val toHexMethod = objectId::class.java.methods.firstOrNull { method ->
            method.name == "toHexString" && method.parameterCount == 0
        }

        return if (toHexMethod != null) {
            runCatching { toHexMethod.invoke(objectId) as? String }.getOrNull()
        } else {
            objectId.toString()
        }
    }
}
