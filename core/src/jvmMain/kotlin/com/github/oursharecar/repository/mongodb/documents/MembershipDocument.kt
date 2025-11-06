package com.github.oursharecar.repository.mongodb.documents

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class MongoMembership(
    @BsonId
    @BsonProperty("_id")
    val id: ObjectId? = null,
    val publicId: String,
    val userId: ObjectId,
    val groupId: ObjectId,
    val role: Role = Role.MEMBER,
    val status: Status = Status.ACTIVE,
    val invitedBy: ObjectId? = null,
    val joinedAt: Instant? = null,
    val leftAt: Instant? = null,
    val audit: AuditableDocument,
) {
    enum class Role { OWNER, ADMIN, MEMBER, VIEWER }
    enum class Status { ACTIVE, INVITED, SUSPENDED, LEFT }

    fun dropId() = copy(id = null)
}