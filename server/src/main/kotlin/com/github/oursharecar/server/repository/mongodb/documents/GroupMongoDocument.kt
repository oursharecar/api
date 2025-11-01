package com.github.oursharecar.server.repository.mongodb.documents

import com.github.oursharecar.server.models.GroupResource
import org.bson.types.ObjectId

data class GroupMongoDocument(
    val _id: ObjectId?,
    val name: String,
    val slug: String,
    val settings: GroupResource.Settings,
    val audit: Auditable
)