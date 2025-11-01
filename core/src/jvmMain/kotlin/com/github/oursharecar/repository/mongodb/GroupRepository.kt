package com.github.oursharecar.repository.mongodb

import com.github.oursharecar.models.Group
import com.github.oursharecar.models.ID
import com.github.oursharecar.repository.Repository
import com.github.oursharecar.repository.WrappedRepository
import com.github.oursharecar.repository.mongodb.documents.Auditable
import com.github.oursharecar.repository.mongodb.documents.GroupMongoDocument
import com.mongodb.kotlin.client.coroutine.MongoCollection
import org.bson.types.ObjectId
import kotlin.time.ExperimentalTime

class GroupRepository(override val impl: Repository<GroupMongoDocument>) :
    WrappedRepository<Group, GroupMongoDocument> {
    constructor(collection: MongoCollection<GroupMongoDocument>) : this(
        MongoRepository(collection)
    )

    @OptIn(ExperimentalTime::class)
    override fun GroupMongoDocument.toResource(): Group {
        return Group(
            id = this._id?.let { ID(it.toHexString()) },
            name = this.name,
            slug = this.slug,
            settings = this.settings,
            audit = com.github.oursharecar.models.Auditable(
                createdAt = this.audit.created_at,
                createdBy = this.audit.created_by,
                updatedAt = this.audit.updated_at,
                updatedBy = this.audit.updated_by,
                deletedAt = this.audit.deleted_at
            )
        )
    }

    @OptIn(ExperimentalTime::class)
    override fun Group.toDocument(): GroupMongoDocument {
        return GroupMongoDocument(
            _id = this.id?.let { ObjectId(it.id) },
            name = this.name,
            slug = this.slug,
            settings = this.settings,
            audit = Auditable(
                created_at = this.audit.createdAt,
                created_by = this.audit.createdBy,
                updated_at = this.audit.updatedAt,
                updated_by = this.audit.updatedBy,
                deleted_at = this.audit.deletedAt
            )
        )
    }

    override fun ID<GroupMongoDocument>.toResourceId(): ID<Group> {
        return ID(this.id)
    }

    override fun ID<Group>.toDocumentId(): ID<GroupMongoDocument> {
        return ID(this.id)
    }
}