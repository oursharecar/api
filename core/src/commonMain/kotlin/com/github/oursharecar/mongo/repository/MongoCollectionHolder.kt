package com.github.oursharecar.mongo.repository

import com.mongodb.kotlin.client.coroutine.MongoCollection

class MongoCollectionHolder<T>(val col: MongoCollection<T>) where T : HasObjectId