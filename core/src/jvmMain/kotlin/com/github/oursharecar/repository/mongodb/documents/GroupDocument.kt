package com.github.oursharecar.repository.mongodb.documents

import com.github.oursharecar.models.GroupResource
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId

data class GroupDocument(
    @BsonId
    @BsonProperty("_id")
    val id: ObjectId?,
    val name: String,
    val slug: String,
    val settings: GroupResource.Settings,
    val audit: AuditableDocument
)