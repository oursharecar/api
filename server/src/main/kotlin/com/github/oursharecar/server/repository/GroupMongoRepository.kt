package com.github.oursharecar.server.repository

import com.github.oursharecar.server.models.GroupResource
import com.github.oursharecar.server.models.ID
import com.mongodb.kotlin.client.coroutine.MongoCollection
import org.bson.types.ObjectId
import kotlin.time.ExperimentalTime

class GroupMongoRepository(override val impl: Repository<GroupMongoDocument>) :
    WrappedRepository<GroupResource, GroupMongoDocument> {
    constructor(collection: MongoCollection<GroupMongoDocument>) : this(
        MongoRepository(collection)
    )

    @OptIn(ExperimentalTime::class)
    override fun GroupMongoDocument.toResource(): GroupResource {
        return GroupResource(
            id = this._id?.let { ID(it.toHexString()) },
            name = this.name,
            slug = this.slug,
            settings = this.settings,
            audit = com.github.oursharecar.server.models.Auditable(
                createdAt = this.audit.created_at,
                createdBy = this.audit.created_by,
                updatedAt = this.audit.updated_at,
                updatedBy = this.audit.updated_by,
                deletedAt = this.audit.deleted_at
            )
        )
    }

    @OptIn(ExperimentalTime::class)
    override fun GroupResource.toDocument(): GroupMongoDocument {
        return GroupMongoDocument(
            _id = this.id?.let { ObjectId(it.id) },
            name = this.name,
            slug = this.slug,
            settings = this.settings,
            audit = GroupMongoDocument.Auditable(
                created_at = this.audit.createdAt,
                created_by = this.audit.createdBy,
                updated_at = this.audit.updatedAt,
                updated_by = this.audit.updatedBy,
                deleted_at = this.audit.deletedAt
            )
        )
    }

    override fun ID<GroupMongoDocument>.toResourceId(): ID<GroupResource> {
        return ID(this.id)
    }

    override fun ID<GroupResource>.toDocumentId(): ID<GroupMongoDocument> {
        return ID(this.id)
    }
}
