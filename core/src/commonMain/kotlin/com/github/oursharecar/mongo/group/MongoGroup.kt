package com.github.oursharecar.mongo.group

import com.github.oursharecar.domain.common.Auditable
import com.github.oursharecar.domain.group.Group
import com.github.oursharecar.mongo.repository.MongoModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class MongoGroup(
    override val name: String,
    override val slug: String,
    override val settings: Group.Settings,
    override val audit: Auditable,
    @SerialName("_id")
    override val id: ObjectId? = null,
) : Group, MongoModel<Group> {
    constructor(group: Group) : this(group.name, group.slug, group.settings, group.audit)

    override fun dropId(): Group = this
}