package com.github.oursharecar.server.routes

import com.github.oursharecar.server.plugins.configureSerialization
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class HealthRouteTest : FunSpec({
    val json = Json {
        ignoreUnknownKeys = true
    }

    test("GET / returns ok status and ISO timestamp") {
        testApplication {
            application {
                configureSerialization()
                routing { healthRoutes() }
            }

            val response = client.get("/")

            response shouldHaveStatus HttpStatusCode.OK
            response.contentType() shouldBe ContentType.Application.Json.withCharset(Charsets.UTF_8)

            val parsed = json.parseToJsonElement(response.bodyAsText()).jsonObject
            parsed["status"]?.jsonPrimitive?.content shouldBe "ok"

            val timestamp = parsed["timestamp"]?.jsonPrimitive?.content
            requireNotNull(timestamp) { "timestamp missing in response" }
            runCatching { Instant.parse(timestamp) }.isSuccess.shouldBeTrue()
        }
    }
})
