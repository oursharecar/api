package com.github.oursharecar.server.plugins

import com.github.oursharecar.telemetry.buildOpenTelemetry
import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import io.opentelemetry.instrumentation.ktor.v3_0.KtorServerTelemetry
import org.slf4j.event.Level

fun Application.configureObservability() {
    val serviceName = environment.config
        .propertyOrNull("ktor.deployment.serviceName")
        ?.getString()
        ?: "oursharecar-api"
    val openTelemetry = buildOpenTelemetry(serviceName)

    install(KtorServerTelemetry) {
        setOpenTelemetry(openTelemetry)
    }
    val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    install(MicrometerMetrics) {
        registry = appMicrometerRegistry
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    routing {
        get("/internal/metrics") {
            call.respond(appMicrometerRegistry.scrape())
        }
    }
}
