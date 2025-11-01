package com.github.oursharecar.repository.mongodb.documents

import com.github.oursharecar.models.GroupResource
import org.bson.types.ObjectId

data class GroupMongoDocument(
    val _id: ObjectId?,
    val name: String,
    val slug: String,
    val settings: GroupResource.Settings,
    val audit: Auditable
)