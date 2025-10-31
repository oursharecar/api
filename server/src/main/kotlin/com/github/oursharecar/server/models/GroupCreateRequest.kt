package com.github.oursharecar.server.models

import com.github.oursharecar.server.utils.GlobalSlugify
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
data class GroupCreateRequest(
    val name: String,
    val members: List<String> = emptyList()
)

@OptIn(ExperimentalTime::class)
fun GroupCreateRequest.buildResource(createdBy: String): GroupResource =
    GroupResource(
        id = null,
        name = name,
        slug = GlobalSlugify.slugify(name),
        settings = GroupResource.Settings(
            visibility = GroupResource.Visibility.PRIVATE,
            joinMode = GroupResource.JoinMode.INVITE,
            memberLimit = 10
        ),
        audit = Auditable(
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now(),
            createdBy = createdBy,
            updatedBy = createdBy,
        )
    )