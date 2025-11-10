package com.github.oursharecar.server.plugins

import io.github.westelh.ktor.simpleCache.SimpleCache
import io.github.westelh.ktor.simpleMemoryCache.memoryCache
import io.ktor.server.application.*
import kotlin.time.Duration.Companion.seconds

fun Application.configureCaching() {
    install(SimpleCache) {
        memoryCache {
            invalidateAt = 30.seconds
        }
    }
}
