package com.github.oursharecar.domain.group

import com.github.oursharecar.domain.common.Auditable

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Group(
    @SerialName("_id")
    val id: String,
    val name: String,
    val slug: String,
    @SerialName("created_by")
    val createdBy: String,
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
