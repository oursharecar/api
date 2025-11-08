package com.github.oursharecar.server.routes

import com.github.oursharecar.models.AuditableResource
import com.github.oursharecar.models.GroupResource
import com.github.oursharecar.models.ID
import com.github.oursharecar.models.UserResource
import com.github.oursharecar.repository.Repository
import com.github.oursharecar.repository.RepositoryFactory
import com.github.oursharecar.server.LinkedMapRepository
import com.github.oursharecar.server.models.GroupCreateRequest
import com.github.oursharecar.server.plugins.configureRouting
import com.github.oursharecar.server.plugins.configureSerialization
import com.github.oursharecar.server.service.ServerServiceImpl
import com.github.oursharecar.server.utils.GlobalSlugify
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class GroupRoutesTest {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @Test
    fun `GET groups returns all available groups`() = testApplication {
        val repository = LinkedMapRepository<GroupResource>()
        val firstGroup = GroupResource(
            id = null,
            name = "Downtown Drivers",
            slug = "downtown-drivers",
            settings = GroupResource.Settings(
                visibility = GroupResource.Visibility.PUBLIC,
                joinMode = GroupResource.JoinMode.OPEN,
                memberLimit = 50
            ),
            audit = audit(
                createdBy = "alice",
                updatedBy = "alice",
                createdAtMillis = 1_000L,
                updatedAtMillis = 2_000L
            )
        )
        val secondGroup = GroupResource(
            id = null,
            name = "Weekend Riders",
            slug = "weekend-riders",
            settings = GroupResource.Settings(
                visibility = GroupResource.Visibility.PRIVATE,
                joinMode = GroupResource.JoinMode.INVITE,
                memberLimit = 10
            ),
            audit = audit(
                createdBy = "bob",
                updatedBy = "bob",
                createdAtMillis = 3_000L,
                updatedAtMillis = 4_000L
            )
        )
        repository.seed("group-1", firstGroup)
        repository.seed("group-2", secondGroup)

        application {
            configureSerialization()
            configureRouting(repository)
        }

        val response = client.get("/groups")

        assertEquals(HttpStatusCode.OK, response.status)
        val decoded = json.decodeFromString<List<GroupResource>>(response.bodyAsText())
        assertEquals(
            listOf(
                firstGroup.copy(id = ID("group-1")),
                secondGroup.copy(id = ID("group-2"))
            ),
            decoded
        )
    }

    @Test
    fun `GET groups id returns existing group`() = testApplication {
        val repository = LinkedMapRepository<GroupResource>()
        val group = GroupResource(
            id = null,
            name = "Neighborhood Carpool",
            slug = "neighborhood-carpool",
            settings = GroupResource.Settings(
                visibility = GroupResource.Visibility.PUBLIC,
                joinMode = GroupResource.JoinMode.REQUEST,
                memberLimit = 25
            ),
            audit = audit(
                createdBy = "carol",
                updatedBy = "carol",
                createdAtMillis = 5_000L,
                updatedAtMillis = 6_000L
            )
        )
        val id = repository.seed("group-42", group)

        application {
            configureSerialization()
            configureRouting(repository)
        }

        val response = client.get("/groups/${id.id}")

        assertEquals(HttpStatusCode.OK, response.status)
        val decoded = json.decodeFromString<GroupResource>(response.bodyAsText())
        assertEquals(group.copy(id = ID("group-42")), decoded)
    }

    @Test
    fun `GET groups id returns 404 for missing group`() = testApplication {
        val repository = LinkedMapRepository<GroupResource>()

        application {
            configureSerialization()
            configureRouting(repository)
        }

        val response = client.get("/groups/group-missing")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `POST groups persists group and returns new id`() = testApplication {
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

        assertEquals(HttpStatusCode.OK, response.status)
        val createdId = Json.decodeFromString<ID<GroupResource>>(response.bodyAsText())

        val stored = runBlocking { repository.findById(createdId) }
        assertNotNull(stored)
        assertEquals("Late Night Cruisers", stored.name)
        assertEquals(GlobalSlugify.slugify("Late Night Cruisers"), stored.slug)
    }

    private fun audit(
        createdBy: String,
        updatedBy: String,
        createdAtMillis: Long,
        updatedAtMillis: Long
    ) = AuditableResource(
        createdAt = Instant.fromEpochMilliseconds(createdAtMillis),
        createdBy = createdBy,
        updatedAt = Instant.fromEpochMilliseconds(updatedAtMillis),
        updatedBy = updatedBy
    )
}

private fun Application.configureRouting(
    groupRepository: Repository<GroupResource> = LinkedMapRepository(),
    userRepository: Repository<UserResource> = LinkedMapRepository()
) {
    configureRouting(ServerServiceImpl(object : RepositoryFactory {
        override fun getGroupRepository() = groupRepository
        override fun getUserRepository() = userRepository
    }))
}