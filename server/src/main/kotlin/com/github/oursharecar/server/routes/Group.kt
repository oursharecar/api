package com.github.oursharecar.server.routes

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val id: String,
    val name: String,
    val members: List<String> = emptyList()
)
