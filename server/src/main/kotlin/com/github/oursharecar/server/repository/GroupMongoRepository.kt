package com.github.oursharecar.server.repository

import com.github.oursharecar.server.models.GroupResource
import com.github.oursharecar.server.models.ID
import com.mongodb.kotlin.client.coroutine.MongoCollection
import org.bson.types.ObjectId

class GroupMongoRepository(override val impl: MongoRepositoryService<GroupMongoDocument>) :
    MongoRepository<GroupResource, GroupMongoDocument> {
    constructor(collection: MongoCollection<GroupMongoDocument>) : this(
        MongoRepositoryService(collection)
    )

    override fun GroupMongoDocument.toResource(): GroupResource {
        return GroupResource(
            id = this._id?.let { ID(it.toHexString()) },
            name = this.name,
            slug = this.slug,
            settings = this.settings,
            audit = this.audit
        )
    }

    override fun GroupResource.toDocument(): GroupMongoDocument {
        return GroupMongoDocument(
            _id = this.id?.let { ObjectId(it.id) },
            name = this.name,
            slug = this.slug,
            settings = this.settings,
            audit = this.audit
        )
    }

    override fun ID<GroupMongoDocument>.toResourceId(): ID<GroupResource> {
        return ID(this.id)
    }

    override fun ID<GroupResource>.toDocumentId(): ID<GroupMongoDocument> {
        return ID(this.id)
    }
}
