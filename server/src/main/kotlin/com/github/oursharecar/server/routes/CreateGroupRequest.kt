package com.github.oursharecar.server.routes

import kotlinx.serialization.Serializable

@Serializable
data class CreateGroupRequest(
    val name: String,
    val members: List<String> = emptyList()
)
