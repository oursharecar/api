package com.github.oursharecar.server.plugins

import com.github.oursharecar.domain.group.GroupRepository
import com.github.oursharecar.server.routes.groupRoutes
import com.github.oursharecar.server.routes.healthRoutes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.conditionalheaders.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(groupRepository: GroupRepository) {
    install(DefaultHeaders) {
        header("X-Service", "oursharecar-api")
    }
    install(ConditionalHeaders)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            this@configureRouting.log.error("Unhandled exception", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to "internal_server_error")
            )
        }
    }

    routing {
        healthRoutes()
        groupRoutes(groupRepository)
    }
}
