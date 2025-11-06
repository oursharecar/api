package com.github.oursharecar.repository.mongodb

import com.github.oursharecar.models.ID
import com.github.oursharecar.repository.Repository
import com.github.oursharecar.repository.WrappedRepository
import com.github.oursharecar.repository.mongodb.documents.Auditable
import com.mongodb.kotlin.client.coroutine.MongoCollection
import org.bson.types.ObjectId
import kotlin.time.ExperimentalTime
import com.github.oursharecar.models.Group as GroupResource
import com.github.oursharecar.repository.mongodb.documents.Group as GroupDocument

class GroupRepository(override val impl: Repository<GroupDocument>) :
    WrappedRepository<GroupResource, GroupDocument> {
    constructor(collection: MongoCollection<GroupDocument>) : this(
        MongoRepository(collection)
    )

    @OptIn(ExperimentalTime::class)
    override fun GroupDocument.toResource(): GroupResource {
        return GroupResource(
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
    override fun GroupResource.toDocument(): GroupDocument {
        return GroupDocument(
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

    override fun ID<GroupDocument>.toResourceId(): ID<GroupResource> {
        return ID(this.id)
    }

    override fun ID<GroupResource>.toDocumentId(): ID<GroupDocument> {
        return ID(this.id)
    }
}