package com.github.oursharecar.server.routes

import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Route.healthRoutes() {
    get("/") {
        call.respond(
            mapOf(
                "status" to "ok",
                "timestamp" to Clock.System.now().toString()
            )
        )
    }
}
