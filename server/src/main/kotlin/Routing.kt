package com.github.oursharecar

import kotlinx.coroutines.withTimeout

import com.ucasoft.ktor.simpleCache.SimpleCache
import com.ucasoft.ktor.simpleCache.cacheOutput
import com.ucasoft.ktor.simpleMemoryCache.memoryCache
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.engine.applicationEnvironment
import io.ktor.server.plugins.conditionalheaders.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
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
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            withContext(NonCancellable) {
                call.respondText("Internal server error",   status = HttpStatusCode.InternalServerError)
            }
        }
    }

    val groups = MongoRepositoryFactory(Config(applicationEnvironment().config)).createGroupRepository()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/groups") {
            val groups = groups.findAll().toList()
            call.respond(groups)
        }
    }
}
