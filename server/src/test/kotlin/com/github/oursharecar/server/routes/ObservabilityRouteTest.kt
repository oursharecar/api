package com.github.oursharecar.server.routes

import com.github.oursharecar.server.plugins.configureObservability
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*

class ObservabilityRouteTest : FunSpec({
    test("GET /internal/metrics exposes Micrometer metrics in Prometheus format") {
        testApplication {
            application { configureObservability() }

            val response = client.get("/internal/metrics")

            response shouldHaveStatus HttpStatusCode.OK
            response.contentType() shouldBe ContentType.Text.Plain.withCharset(Charsets.UTF_8)

            val body = response.bodyAsText()
            body shouldContain "# HELP"
            body shouldContain "jvm_threads_live_threads"
        }
    }
})
