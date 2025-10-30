package com.github.oursharecar.mongo.repository

import com.github.oursharecar.domain.common.ID
import org.bson.types.ObjectId

interface HasId<E> {
    val id: ID<E>?
}

internal fun <E> HasId<E>.objectId(): ObjectId? {
    val id = this.id
    return if (id != null) {
        return ObjectId(id.id)
    } else null
}
