package com.github.oursharecar.server.fixtures

import com.github.oursharecar.models.AuditableResource
import com.github.oursharecar.models.GroupResource
import com.github.oursharecar.models.ID
import com.github.oursharecar.server.utils.GlobalSlugify
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal fun sampleGroup(
    idValue: String? = null,
    name: String = "Downtown Drivers",
    slug: String = GlobalSlugify.slugify(name),
    visibility: GroupResource.Visibility = GroupResource.Visibility.PUBLIC,
    joinMode: GroupResource.JoinMode = GroupResource.JoinMode.REQUEST,
    memberLimit: Int = 25,
    createdBy: String = "tester",
    updatedBy: String = createdBy,
    createdAtMillis: Long = 1_000L,
    updatedAtMillis: Long = 2_000L
): GroupResource =
    GroupResource(
        id = idValue?.let { ID(it) },
        name = name,
        slug = slug,
        settings = GroupResource.Settings(
            visibility = visibility,
            joinMode = joinMode,
            memberLimit = memberLimit
        ),
        audit = AuditableResource(
            createdAt = Instant.fromEpochMilliseconds(createdAtMillis),
            updatedAt = Instant.fromEpochMilliseconds(updatedAtMillis),
            createdBy = createdBy,
            updatedBy = updatedBy
        )
    )
