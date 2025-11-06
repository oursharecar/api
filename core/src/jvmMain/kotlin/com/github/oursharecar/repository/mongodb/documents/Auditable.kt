package com.github.oursharecar.repository.mongodb.documents

import org.bson.codecs.pojo.annotations.BsonProperty
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class Auditable(
    @BsonProperty("created_at")
    val createdAt: Instant,
    @BsonProperty("created_by")
    val createdBy: String,
    @BsonProperty("updated_at")
    val updatedAt: Instant,
    @BsonProperty("updated_by")
    val updatedBy: String,
    @BsonProperty("deleted_at")
    val deletedAt: Instant? = null
)