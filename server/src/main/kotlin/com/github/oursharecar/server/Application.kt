package com.github.oursharecar.server

import com.github.oursharecar.server.config.MongoConfig
import com.github.oursharecar.server.plugins.*
import com.github.oursharecar.server.repository.mongodb.MongoRepositoryFactory
import io.ktor.server.application.*
import io.ktor.server.cio.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    val factory = MongoRepositoryFactory(MongoConfig(environment.config))

    configureSerialization()
    configureAdministration()
    configureObservability()
    configureSecurity()
    configureStatusPages()
    configureRouting(factory.getGroupRepository())
}
