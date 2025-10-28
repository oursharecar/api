package com.github.oursharecar

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val id: String,
    val name: String,
    val slug: String,
    val createdBy: String,
    val settings: Settings
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
