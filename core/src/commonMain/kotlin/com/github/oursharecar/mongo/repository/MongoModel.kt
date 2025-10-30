package com.github.oursharecar.mongo.repository

interface MongoModel<T> : HasObjectId {
    fun withoutId(): T
}