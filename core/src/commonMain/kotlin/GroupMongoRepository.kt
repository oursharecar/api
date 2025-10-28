package com.github.oursharecar

import com.mongodb.kotlin.client.coroutine.MongoDatabase

class GroupMongoRepository(database: MongoDatabase, collectionName: String) : MongoRepository<String, Group>(database.getCollection(collectionName), { it.id }) {
}