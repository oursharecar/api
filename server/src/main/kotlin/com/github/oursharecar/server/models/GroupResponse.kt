package com.github.oursharecar.server.models

import com.github.oursharecar.domain.group.Group
import kotlinx.serialization.Serializable

@Serializable
data class GroupResponse(
    val id: String,
    val name: String,
)

fun Group.asResponse() = GroupResponse(
    id = this.id?.id ?: "",
    name = this.name,
)