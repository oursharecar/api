package com.github.oursharecar.server

import com.github.oursharecar.mongo.factory.MongoRepositoryFactory
import com.github.oursharecar.server.config.MongoConfig
import com.github.oursharecar.server.plugins.configureAdministration
import com.github.oursharecar.server.plugins.configureObservability
import com.github.oursharecar.server.plugins.configureRouting
import com.github.oursharecar.server.plugins.configureSecurity
import com.github.oursharecar.server.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.cio.EngineMain

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    val mongoConfig = MongoConfig(environment.config)
    val repositoryFactory = MongoRepositoryFactory(mongoConfig)

    configureSerialization()
    configureAdministration()
    configureObservability()
    configureSecurity()
    configureRouting(repositoryFactory.createGroupRepository())
}
