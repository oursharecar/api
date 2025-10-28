package com.github.oursharecar.server.plugins

import io.ktor.server.application.*
import io.ktor.server.engine.*

fun Application.configureAdministration() {
    install(ShutDownUrl.ApplicationCallPlugin) {
        shutDownUrl = this@configureAdministration.environment.config
            .propertyOrNull("ktor.deployment.shutdown.url")
            ?.getString()
            ?: "/internal/shutdown"
        exitCodeSupplier = { 0 }
    }
}
