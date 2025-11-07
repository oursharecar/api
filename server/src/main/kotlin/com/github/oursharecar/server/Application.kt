package com.github.oursharecar.server

import com.github.oursharecar.repository.mongodb.MongoRepositoryFactory
import com.github.oursharecar.server.config.MongoConfig
import com.github.oursharecar.server.plugins.*
import com.github.oursharecar.server.service.ServerServiceImpl
import io.ktor.server.application.*
import io.ktor.server.cio.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    val factory = MongoRepositoryFactory(MongoConfig(environment.config))
    val service = ServerServiceImpl(factory)

    configureSerialization()
    configureAdministration()
    configureObservability()
    configureSecurity()
    configureStatusPages()
    configureRouting(service)
}
