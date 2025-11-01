package com.github.oursharecar.repository.mongodb.documents

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class Auditable(
    val created_at: Instant,
    val created_by: String,
    val updated_at: Instant,
    val updated_by: String,
    val deleted_at: Instant? = null
)