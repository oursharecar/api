package com.github.oursharecar.domain.group

import com.github.oursharecar.domain.common.Auditable
import com.github.oursharecar.domain.common.ID

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Group(
    @SerialName("_id")
    val id: ID<Group>? = null,
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
