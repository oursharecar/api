package com.github.oursharecar

import com.ucasoft.ktor.simpleCache.SimpleCache
import com.ucasoft.ktor.simpleCache.cacheOutput
import com.ucasoft.ktor.simpleMemoryCache.memoryCache
import io.ktor.server.application.*
import io.ktor.server.plugins.conditionalheaders.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

fun Application.configureRouting() {
    install(SimpleCache) {
        memoryCache {
            invalidateAt = 10.seconds
        }
    }
    install(DefaultHeaders) {
        header("X-Engine", "Ktor")
    }
    install(ConditionalHeaders)
    routing {
        cacheOutput(2.seconds) {
            get("/short") {
                call.respond(Random.nextInt().toString())
            }
        }
        cacheOutput {
            get("/default") {
                call.respond(Random.nextInt().toString())
            }
        }
        get("/") {
            call.respondText("Hello World!")
        }
    }
}
