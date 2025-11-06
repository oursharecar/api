package com.github.oursharecar.repository.mongodb.documents

import com.github.oursharecar.models.Group
import org.bson.types.ObjectId

data class Group(
    val _id: ObjectId?,
    val name: String,
    val slug: String,
    val settings: Group.Settings,
    val audit: Auditable
)