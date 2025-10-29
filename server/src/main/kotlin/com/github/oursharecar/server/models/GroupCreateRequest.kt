package com.github.oursharecar.server.models

import kotlinx.serialization.Serializable

@Serializable
data class GroupCreateRequest(
    val name: String,
    val members: List<String> = emptyList()
)