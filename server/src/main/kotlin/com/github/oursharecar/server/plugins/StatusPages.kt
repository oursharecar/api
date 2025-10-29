package com.github.oursharecar.server.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    val log = this.log

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            log.error("Unhandled exception", cause)
            when (cause) {
                is BadRequestException -> call.respond(
                    HttpStatusCode.BadRequest,
                    message = mapOf("error" to cause.message)
                )

                else -> call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "internal_server_error"))
            }
        }
    }
}