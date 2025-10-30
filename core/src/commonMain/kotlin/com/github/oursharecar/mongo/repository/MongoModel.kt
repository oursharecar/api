package com.github.oursharecar.mongo.repository

import com.github.oursharecar.domain.group.DomainModel

interface MongoModel<T : DomainModel> : HasObjectId {
    fun withoutId(): T
}