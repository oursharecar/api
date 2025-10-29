package com.github.oursharecar.server

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.*

class ApplicationTest : FunSpec({
    val jwtConfig = MapApplicationConfig(
        "security.jwt.audience" to "test-audience",
        "security.jwt.realm" to "test-realm",
        "security.jwt.issuer" to "test-issuer",
        "security.jwt.secret" to "a-string-secret-at-least-256-bits-long"
    )

    val exampleValidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30"

    test("GET / returns health status payload") {
        testApplication {
            application { module() }
            environment { config = jwtConfig }

            val response = client.get("/")
            response.status shouldBe HttpStatusCode.OK
            response.bodyAsText() shouldContain "\"status\":\"ok\""
        }
    }
})
