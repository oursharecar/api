package com.github.oursharecar.server

import com.github.oursharecar.server.config.MongoConfig
import com.github.oursharecar.server.plugins.*
import com.github.oursharecar.server.repository.GroupMongoRepository
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.ktor.server.application.*
import io.ktor.server.cio.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    val mongoConfig = MongoConfig(environment.config)
    val mongoClient = MongoClient.create()
    val mongoDatabase = mongoClient.getDatabase(mongoConfig.databaseName)

    configureSerialization()
    configureAdministration()
    configureObservability()
    configureSecurity()
    configureStatusPages()
    configureRouting(GroupMongoRepository(mongoDatabase.getCollection(mongoConfig.groupCollectionName)))
}
