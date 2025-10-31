package com.github.oursharecar.server.repository

import com.github.oursharecar.server.models.Auditable
import com.github.oursharecar.server.models.GroupResource
import com.github.oursharecar.server.models.ID
import org.bson.types.ObjectId

data class GroupMongoDocument(
    val _id: ObjectId?,
    val name: String,
    val slug: String,
    val settings: GroupResource.Settings,
    val audit: Auditable
)

fun GroupMongoDocument.toGroupResource(): GroupResource {
    return GroupResource(
        id = _id?.let { ID(it.toHexString()) },
        name = name,
        slug = slug,
        settings = settings,
        audit = audit
    )
}