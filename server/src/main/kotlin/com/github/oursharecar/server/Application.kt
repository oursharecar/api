package com.github.oursharecar.server

import com.github.oursharecar.mongo.factory.MongoRepositoryFactory
import com.github.oursharecar.server.config.MongoConfig
import com.github.oursharecar.server.plugins.*
import io.ktor.server.application.*
import io.ktor.server.cio.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    val mongoConfig = MongoConfig(environment.config)
    val repositoryFactory = MongoRepositoryFactory(mongoConfig)

    configureSerialization()
    configureAdministration()
    configureObservability()
    configureSecurity()
    configureStatusPages()
    configureRouting(repositoryFactory.createGroupRepository())
}
