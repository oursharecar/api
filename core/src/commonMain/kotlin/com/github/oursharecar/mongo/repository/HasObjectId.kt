package com.github.oursharecar.mongo.repository

import org.bson.types.ObjectId

interface HasObjectId {
    val id: ObjectId?
}