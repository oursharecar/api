package com.github.oursharecar.repository.mongodb

import com.github.oursharecar.models.ID
import com.github.oursharecar.models.UserResource
import com.github.oursharecar.repository.Repository
import com.github.oursharecar.repository.WrappedRepository
import com.github.oursharecar.repository.mongodb.documents.UserDocument
import com.mongodb.kotlin.client.coroutine.MongoCollection

class UserRepository(override val impl: Repository<UserDocument>) :
    WrappedRepository<UserResource, UserDocument> {
    constructor(collection: MongoCollection<UserDocument>) : this(
        MongoRepository(collection)
    )

    override fun UserDocument.toResource(): UserResource {
        TODO("Not yet implemented")
    }

    override fun UserResource.toDocument(): UserDocument {
        TODO("Not yet implemented")
    }

    override fun ID<UserDocument>.toResourceId(): ID<UserResource> {
        TODO("Not yet implemented")
    }

    override fun ID<UserResource>.toDocumentId(): ID<UserDocument> {
        TODO("Not yet implemented")
    }
}
