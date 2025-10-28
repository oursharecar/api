package com.github.oursharecar.server.config

import com.github.oursharecar.mongo.config.MongoRepositoryConfig
import io.ktor.server.config.*

class MongoConfig(config: ApplicationConfig) : MongoRepositoryConfig {
    private val path = config.config("mongodb")

    override val connectionString: String? =
        path.propertyOrNull("connectionString")?.getString()
    override val databaseName: String =
        path.propertyOrNull("database")?.getString() ?: "oursharecar"
    override val groupCollectionName: String =
        path.propertyOrNull("collections.group")?.getString() ?: "groups"
}
