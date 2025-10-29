package com.github.oursharecar.server.models

import com.github.oursharecar.domain.common.Auditable
import com.github.oursharecar.domain.group.Group
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
fun newDomainObjectFromRequest(request: GroupCreateRequest, createdBy: String): Group = with(request) {
    Group(
        name = name,
        slug = GlobalSlugify.slugify(name),
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
}