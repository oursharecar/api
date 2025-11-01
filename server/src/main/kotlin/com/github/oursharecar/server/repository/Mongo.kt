package com.github.oursharecar.server.repository

import com.github.oursharecar.server.models.ID
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import org.bson.conversions.Bson
import org.bson.types.ObjectId

private object IdConverter {
    fun convert(id: ID<*>): ObjectId = ObjectId(id.id)
    fun <T> convert(id: ObjectId): ID<T> = ID(id.toHexString())
}

suspend fun <T : Any> MongoCollection<T>.findById(id: ID<*>): T? {
    return this.findById(IdConverter.convert(id))
}

suspend fun <T : Any> MongoCollection<T>.findById(id: ObjectId, vararg filters: Bson): T? {
    return this.find(Filters.and(*filters, Filters.eq("_id", id)))
        .limit(1)
        .firstOrNull()
}
