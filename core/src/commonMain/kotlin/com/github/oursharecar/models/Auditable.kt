package com.github.oursharecar.models

import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.serializers.FormattedInstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object Rfc1123Serializer : FormattedInstantSerializer(
    "com.github.oursharecar.server.models.Rfc1123Serializer",
    DateTimeComponents.Formats.RFC_1123
)

/**
 * Represents audit metadata tracked for persisted entities.
 */
@OptIn(ExperimentalTime::class)
@Serializable
data class Auditable(
    @Serializable(with = Rfc1123Serializer::class)
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("created_by")
    val createdBy: String,
    @Serializable(with = Rfc1123Serializer::class)
    @SerialName("updated_at")
    val updatedAt: Instant,
    @SerialName("updated_by")
    val updatedBy: String,
    @Serializable(with = Rfc1123Serializer::class)
    @SerialName("deleted_at")
    val deletedAt: Instant? = null
)