package com.github.oursharecar.server.plugins

import com.github.oursharecar.server.models.GroupResource
import com.github.oursharecar.server.repository.Repository
import com.github.oursharecar.server.routes.groupRoutes
import com.github.oursharecar.server.routes.healthRoutes
import io.ktor.server.application.*
import io.ktor.server.plugins.conditionalheaders.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*

fun Application.configureRouting(groupRepository: Repository<GroupResource>) {
    install(DefaultHeaders) {
        header("X-Service", "oursharecar-api")
    }
    install(ConditionalHeaders)
    install(Resources)

    routing {
        healthRoutes()
        groupRoutes(groupRepository)
    }
}
