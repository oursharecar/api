package com.github.oursharecar.server.routes

import com.github.oursharecar.models.GroupResource
import com.github.oursharecar.models.ID
import com.github.oursharecar.server.fixtures.sampleGroup
import com.github.oursharecar.server.models.GroupCreateRequest
import com.github.oursharecar.server.plugins.configureRouting
import com.github.oursharecar.server.plugins.configureSerialization
import com.github.oursharecar.server.service.ServerService
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.serialization.json.Json

class GroupRoutesTest : FunSpec({
    val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    test("GET /groups returns groups provided by the service") {
        testApplication {
            val stubbedGroups = listOf(
                sampleGroup(idValue = "group-1", name = "Downtown Drivers"),
                sampleGroup(idValue = "group-2", name = "Weekend Riders")
            )
            val service = mockk<ServerService> {
                coEvery { listGroups() } returns stubbedGroups
            }

            application { configureTestRouting(service) }

            val response = client.get("/groups")

            response shouldHaveStatus HttpStatusCode.OK
            val decoded = json.decodeFromString<List<GroupResource>>(response.bodyAsText())
            decoded shouldBe stubbedGroups
            coVerify(exactly = 1) { service.listGroups() }
        }
    }

    test("GET /groups/{id} returns existing group from the service") {
        testApplication {
            val expected = sampleGroup(idValue = "group-42", name = "Neighborhood Carpool")
            val service = mockk<ServerService> {
                coEvery { getGroup(ID("group-42")) } returns expected
            }

            application { configureTestRouting(service) }

            val response = client.get("/groups/group-42")

            response shouldHaveStatus HttpStatusCode.OK
            val decoded = json.decodeFromString<GroupResource>(response.bodyAsText())
            decoded shouldBe expected
            coVerify(exactly = 1) { service.getGroup(ID("group-42")) }
        }
    }

    test("GET /groups/{id} returns 404 when the service cannot find the group") {
        testApplication {
            val service = mockk<ServerService> {
                coEvery { getGroup(ID("group-missing")) } returns null
            }

            application { configureTestRouting(service) }

            val response = client.get("/groups/group-missing")

            response shouldHaveStatus HttpStatusCode.NotFound
            coVerify(exactly = 1) { service.getGroup(ID("group-missing")) }
        }
    }

    test("POST /groups forwards payload to the service and returns new id") {
        testApplication {
            val service = mockk<ServerService> {
                coEvery { createGroup(any()) } returns ID("group-777")
            }

            application { configureTestRouting(service) }

            val request = GroupCreateRequest(name = "Late Night Cruisers")
            val response = client.post("/groups") {
                setBody(
                    TextContent(
                        text = json.encodeToString(request),
                        contentType = ContentType.Application.Json
                    )
                )
            }

            response shouldHaveStatus HttpStatusCode.OK
            val createdId = json.decodeFromString<ID<GroupResource>>(response.bodyAsText())
            createdId shouldBe ID("group-777")
            coVerify(exactly = 1) { service.createGroup(request) }
        }
    }

    test("DELETE /groups/{id} responds with 204 and signals the service") {
        testApplication {
            val service = mockk<ServerService> {
                coEvery { deleteGroup(ID("group-9")) } returns true
            }

            application { configureTestRouting(service) }

            val response = client.delete("/groups/group-9")

            response shouldHaveStatus HttpStatusCode.NoContent
            coVerify(exactly = 1) { service.deleteGroup(ID("group-9")) }
        }
    }
})

private fun Application.configureTestRouting(service: ServerService) {
    configureSerialization()
    configureRouting(service)
}
