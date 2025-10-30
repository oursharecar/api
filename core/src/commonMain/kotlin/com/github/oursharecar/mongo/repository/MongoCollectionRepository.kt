package com.github.oursharecar.mongo.repository

import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.Flow

class MongoCollectionRepository<T : HasObjectId>(val holder: MongoCollectionHolder<T>) {
    constructor(collection: MongoCollection<T>) : this(MongoCollectionHolder(collection))

    fun findAll(): Flow<T> = holder.col.find()
}