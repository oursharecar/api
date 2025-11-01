package com.github.oursharecar.server.repository

import com.github.oursharecar.server.models.GroupResource
import org.bson.types.ObjectId
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class GroupMongoDocument(
    val _id: ObjectId?,
    val name: String,
    val slug: String,
    val settings: GroupResource.Settings,
    val audit: Auditable
) {
    @OptIn(ExperimentalTime::class)
    data class Auditable(
        val created_at: Instant,
        val created_by: String,
        val updated_at: Instant,
        val updated_by: String,
        val deleted_at: Instant? = null
    )
}