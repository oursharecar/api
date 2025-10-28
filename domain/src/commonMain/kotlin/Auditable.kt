package com.github.oursharecar

import kotlinx.serialization.SerialName
import kotlin.time.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.InstantComponentSerializer
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Serializable
open class Auditable (
    @Serializable(with = InstantComponentSerializer::class)
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("created_by")
    val createdBy: String,
    @Serializable(with = InstantComponentSerializer::class)
    @SerialName("updated_at")
    val updatedAt: Instant,
    @SerialName("updated_by")
    val updatedBy: String,
    @Serializable(with = InstantComponentSerializer::class)
    @SerialName("deleted_at")
    val deletedAt: Instant? = null
)

@Serializable
open class TenantScoped(val tenantId: String)