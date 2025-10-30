package com.github.oursharecar.server.models

import com.github.oursharecar.mongo.group.MongoGroup
import kotlinx.serialization.Serializable

@Serializable
data class GroupResponse(
    val id: String,
    val name: String,
)

fun MongoGroup.asResponse() = GroupResponse(
    id = this.id?.id ?: "",
    name = this.name,
)