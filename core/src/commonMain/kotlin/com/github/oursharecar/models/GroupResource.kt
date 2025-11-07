package com.github.oursharecar.models

import kotlinx.serialization.Serializable

@Serializable
data class GroupResource(
    override val id: ID<GroupResource>?,
    val name: String,
    val slug: String,
    val settings: Settings,
    val audit: AuditableResource
) : Identifiable<GroupResource> {
    @Serializable
    data class Settings(
        val visibility: Visibility,
        val joinMode: JoinMode,
        val memberLimit: Int = 10
    )

    enum class Visibility { PUBLIC, PRIVATE }
    enum class JoinMode { INVITE, REQUEST, OPEN }

    override fun withId(id: ID<GroupResource>): GroupResource = copy(id = id)
}
