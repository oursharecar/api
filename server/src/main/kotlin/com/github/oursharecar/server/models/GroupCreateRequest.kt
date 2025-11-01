package com.github.oursharecar.server.models

import com.github.oursharecar.models.Auditable
import com.github.oursharecar.models.Group
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
data class GroupCreateRequest(
    val name: String,
    val members: List<String> = emptyList()
)

@OptIn(ExperimentalTime::class)
fun GroupCreateRequest.buildResource(
    createdBy: String,
    slug: String,
): Group =
    Group(
        id = null,
        name = name,
        slug = slug,
        settings = Group.Settings(
            visibility = Group.Visibility.PRIVATE,
            joinMode = Group.JoinMode.INVITE,
            memberLimit = 10
        ),
        audit = Auditable(
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now(),
            createdBy = createdBy,
            updatedBy = createdBy,
        )
    )