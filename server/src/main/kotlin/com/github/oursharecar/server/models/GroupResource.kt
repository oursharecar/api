package com.github.oursharecar.server.models

import com.github.oursharecar.domain.common.Auditable
import com.github.oursharecar.domain.group.Group
import kotlinx.serialization.Serializable

/**
 * Serializable representation of a [Group] exposed by the HTTP layer.
 */
@Serializable
data class GroupResource(
    val name: String,
    val slug: String,
    val settings: Group.Settings,
    val audit: Auditable
)

fun Group.asResource(): GroupResource =
    GroupResource(
        name = name,
        slug = slug,
        settings = settings,
        audit = audit
    )
