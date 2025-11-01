package com.github.oursharecar.models

import kotlinx.serialization.Serializable

@Serializable
data class GroupResource(
    val id: ID<GroupResource>?,
    val name: String,
    val slug: String,
    val settings: Settings,
    val audit: Auditable
) {
    @Serializable
    data class Settings(
        val visibility: Visibility,
        val joinMode: JoinMode,
        val memberLimit: Int = 10
    )

    enum class Visibility { PUBLIC, PRIVATE }
    enum class JoinMode { INVITE, REQUEST, OPEN }
}