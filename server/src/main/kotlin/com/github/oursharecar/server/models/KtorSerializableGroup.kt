package com.github.oursharecar.server.models

import com.github.oursharecar.domain.common.Auditable
import com.github.oursharecar.domain.group.Group
import kotlinx.serialization.Serializable

@Serializable
data class KtorSerializableGroup(
    override val name: String,
    override val slug: String,
    override val settings: Group.Settings,
    override val audit: Auditable
): Group

fun Group.ktorSerializable(): KtorSerializableGroup {
    return KtorSerializableGroup(
        this.name,
        this.slug,
        this.settings,
        this.audit
    )
}
