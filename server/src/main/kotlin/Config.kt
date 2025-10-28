package com.github.oursharecar

import io.ktor.server.config.ApplicationConfig

class Config(config: ApplicationConfig) : MongoRepositoryConfig {
    private val path = config.config("mongodb")
    override val connectionString = path.propertyOrNull("connectionString")?.getString()
    override val databaseName: String = path.propertyOrNull("database")?.getString() ?: "oursharecar"
    override val groupCollectionName: String = path.propertyOrNull("collections.group")?.getString() ?: "groups"
}