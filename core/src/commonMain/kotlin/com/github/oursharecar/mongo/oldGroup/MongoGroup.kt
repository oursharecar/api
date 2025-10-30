package com.github.oursharecar.mongo.oldGroup

import com.github.oursharecar.domain.common.Auditable
import com.github.oursharecar.domain.common.ID
import com.github.oursharecar.domain.group.Group
import com.github.oursharecar.mongo.repository.HasId
import kotlinx.serialization.Serializable

@Serializable
data class MongoGroup(
    override val name: String,
    override val slug: String,
    override val settings: Group.Settings,
    override val audit: Auditable,
    override val id: ID<MongoGroup>? = null,
) : Group, HasId<MongoGroup>