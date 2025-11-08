package com.github.oursharecar.server.routes

import com.github.oursharecar.models.GroupResource
import com.github.oursharecar.models.ID
import com.github.oursharecar.models.UserResource
import com.github.oursharecar.repository.Repository
import com.github.oursharecar.repository.RepositoryFactory
import com.github.oursharecar.server.LinkedMapRepository
import com.github.oursharecar.server.fixtures.sampleGroup
import com.github.oursharecar.server.models.GroupCreateRequest
import com.github.oursharecar.server.plugins.configureRouting
import com.github.oursharecar.server.plugins.configureSerialization
import com.github.oursharecar.server.service.ServerServiceImpl
import com.github.oursharecar.server.utils.GlobalSlugify
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class GroupRoutesTest : FunSpec({
    val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    test("GET /groups returns all available groups") {
        testApplication {
            val repository = LinkedMapRepository<GroupResource>()
            val firstGroup = sampleGroup(
                name = "Downtown Drivers",
                visibility = GroupResource.Visibility.PUBLIC,
                joinMode = GroupResource.JoinMode.OPEN,
                memberLimit = 50,
                createdBy = "alice",
                updatedBy = "alice",
                createdAtMillis = 1_000L,
                updatedAtMillis = 2_000L
            )
            val secondGroup = sampleGroup(
                name = "Weekend Riders",
                visibility = GroupResource.Visibility.PRIVATE,
                joinMode = GroupResource.JoinMode.INVITE,
                memberLimit = 10,
                createdBy = "bob",
                updatedBy = "bob",
                createdAtMillis = 3_000L,
                updatedAtMillis = 4_000L
            )
            repository.seed("group-1", firstGroup)
            repository.seed("group-2", secondGroup)

            application {
                configureSerialization()
                configureRouting(repository)
            }

            val response = client.get("/groups")

            response shouldHaveStatus HttpStatusCode.OK
            val decoded = json.decodeFromString<List<GroupResource>>(response.bodyAsText())
            decoded shouldBe listOf(
                firstGroup.copy(id = ID("group-1")),
                secondGroup.copy(id = ID("group-2"))
            )
        }
    }

    test("GET /groups/{id} returns existing group") {
        testApplication {
            val repository = LinkedMapRepository<GroupResource>()
            val group = sampleGroup(
                name = "Neighborhood Carpool",
                createdBy = "carol",
                updatedBy = "carol",
                createdAtMillis = 5_000L,
                updatedAtMillis = 6_000L
            )
            val id = repository.seed("group-42", group)

            application {
                configureSerialization()
                configureRouting(repository)
            }

            val response = client.get("/groups/${id.id}")

            response shouldHaveStatus HttpStatusCode.OK
            val decoded = json.decodeFromString<GroupResource>(response.bodyAsText())
            decoded shouldBe group.copy(id = ID("group-42"))
        }
    }

    test("GET /groups/{id} returns 404 when missing") {
        testApplication {
            val repository = LinkedMapRepository<GroupResource>()

            application {
                configureSerialization()
                configureRouting(repository)
            }

            val response = client.get("/groups/group-missing")

            response shouldHaveStatus HttpStatusCode.NotFound
        }
    }

    test("POST /groups persists group and returns new id") {
        testApplication {
            val repository = LinkedMapRepository<GroupResource>()

            application {
                configureSerialization()
                configureRouting(repository)
            }

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
            val createdId = Json.decodeFromString<ID<GroupResource>>(response.bodyAsText())

            val stored = runBlocking { repository.findById(createdId) }.shouldNotBeNull()
            stored.name shouldBe "Late Night Cruisers"
            stored.slug shouldBe GlobalSlugify.slugify("Late Night Cruisers")
        }
    }
})

private fun Application.configureRouting(
    groupRepository: Repository<GroupResource> = LinkedMapRepository(),
    userRepository: Repository<UserResource> = LinkedMapRepository()
) {
    configureRouting(ServerServiceImpl(object : RepositoryFactory {
        override fun getGroupRepository() = groupRepository
        override fun getUserRepository() = userRepository
    }))
}
