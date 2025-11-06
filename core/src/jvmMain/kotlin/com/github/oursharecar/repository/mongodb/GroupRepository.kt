package com.github.oursharecar.repository.mongodb

import com.github.oursharecar.models.AuditableResource
import com.github.oursharecar.models.GroupResource
import com.github.oursharecar.models.ID
import com.github.oursharecar.repository.Repository
import com.github.oursharecar.repository.WrappedRepository
import com.github.oursharecar.repository.mongodb.documents.Auditable
import com.mongodb.kotlin.client.coroutine.MongoCollection
import org.bson.types.ObjectId
import kotlin.time.ExperimentalTime
import com.github.oursharecar.repository.mongodb.documents.Group as GroupDocument

class GroupRepository(override val impl: Repository<GroupDocument>) :
    WrappedRepository<GroupResource, GroupDocument> {
    constructor(collection: MongoCollection<GroupDocument>) : this(
        MongoRepository(collection)
    )

    @OptIn(ExperimentalTime::class)
    override fun GroupDocument.toResource(): GroupResource {
        return GroupResource(
            id = this.id?.let { ID(it.toHexString()) },
            name = this.name,
            slug = this.slug,
            settings = this.settings,
            audit = AuditableResource(
                createdAt = this.audit.createdAt,
                createdBy = this.audit.createdBy,
                updatedAt = this.audit.updatedAt,
                updatedBy = this.audit.updatedBy,
                deletedAt = this.audit.deletedAt
            )
        )
    }

    @OptIn(ExperimentalTime::class)
    override fun GroupResource.toDocument(): GroupDocument {
        return GroupDocument(
            id = this.id?.let { ObjectId(it.id) },
            name = this.name,
            slug = this.slug,
            settings = this.settings,
            audit = Auditable(
                createdAt = this.audit.createdAt,
                createdBy = this.audit.createdBy,
                updatedAt = this.audit.updatedAt,
                updatedBy = this.audit.updatedBy,
                deletedAt = this.audit.deletedAt
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