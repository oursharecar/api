package com.github.oursharecar.server.routes

import com.github.oursharecar.domain.common.*
import com.github.oursharecar.domain.group.Group
import com.github.oursharecar.domain.group.GroupRepository
import com.github.oursharecar.server.models.GroupCreateRequest
import com.github.oursharecar.server.models.GroupResponse
import com.github.oursharecar.server.models.asResponse
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class GroupRoutesTest {

    private val json = Json {
        encodeDefaults = true
        serializersModule = DomainSerializersModule
    }

    @Test
    fun `GET groups returns repository contents`() = testApplication {
        val groups = listOf(
            sampleGroup(id = ID("group-1"), name = "Neighborhood EVs", slug = "neighborhood-evs"),
            sampleGroup(id = ID("group-2"), name = "Downtown Car Share", slug = "downtown-car-share")
        )
        val repository = FakeGroupRepository(groups)

        application {
            install(ContentNegotiation) {
                json(json)
            }
            install(Resources)
            routing {
                groupRoutes(repository)
            }
        }
        val client = createClient { expectSuccess = false }

        val response = client.get("/groups")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        val payload = json.decodeFromString<List<Group>>(body)
        assertEquals(groups, payload)
    }

    @Test
    fun `GET group by id returns matching group`() = testApplication {
        val group = sampleGroup(id = ID("existing"), name = "Existing Group", slug = "existing")
        val repository = FakeGroupRepository(listOf(group))
        val existingId = requireNotNull(group.id).id

        application {
            install(ContentNegotiation) {
                json(json)
            }
            install(Resources)
            routing {
                groupRoutes(repository)
            }
        }
        val client = createClient { expectSuccess = false }

        val response = client.get("/groups/$existingId")
        val status = response.status
        val body = response.bodyAsText()

        assertEquals(HttpStatusCode.OK, status, "response body: $body")
        val payload = json.decodeFromString(GroupResponse.serializer(), body)
        assertEquals(group.asResponse(), payload)
    }

    @Test
    fun `GET group by id returns 404 when missing`() = testApplication {
        val repository = FakeGroupRepository(emptyList())

        application {
            install(ContentNegotiation) {
                json(json)
            }
            install(Resources)
            routing {
                groupRoutes(repository)
            }
        }
        val client = createClient { expectSuccess = false }

        val response = client.get("/groups/missing-group")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `POST groups responds with created id`() = testApplication {
        val repository = FakeGroupRepository(emptyList())
        val request = GroupCreateRequest(
            name = "New Group",
            members = listOf("alice", "bob")
        )

        application {
            install(ContentNegotiation) {
                json(json)
            }
            install(Resources)
            routing {
                groupRoutes(repository)
            }
        }
        val client = createClient { expectSuccess = false }

        val response = client.post("/groups") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(GroupCreateRequest.serializer(), request))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertEquals("group-1", body)
    }

    @OptIn(ExperimentalTime::class)
    private fun sampleGroup(id: ID<Group>, name: String, slug: String): Group {
        val audit = Auditable(
            createdAt = Instant.fromEpochMilliseconds(0),
            createdBy = "system",
            updatedAt = Instant.fromEpochMilliseconds(0),
            updatedBy = "system"
        )
        return Group(
            id = id,
            name = name,
            slug = slug,
            settings = Group.Settings(
                visibility = Group.Visibility.PUBLIC,
                joinMode = Group.JoinMode.OPEN,
                memberLimit = 10
            ),
            audit = audit
        )
    }
}

private class FakeGroupRepository(initialGroups: List<Group>) : GroupRepository {

    private val groups = initialGroups.toMutableList()

    override suspend fun findById(id: ID<Group>): Group? {
        val result = groups.find { it.id == id }
        println("FakeGroupRepository.findById($id) -> ${result != null}")
        return result
    }

    override suspend fun existsById(id: ID<Group>): Boolean = groups.any { it.id == id }

    override suspend fun insert(entity: Group): ID<Group> {
        val assignedId = entity.id.takeIf { !it?.id.isNullOrBlank() } ?: ID("group-${groups.size + 1}")
        groups += entity.copy(id = assignedId)
        return assignedId
    }

    override suspend fun upsert(id: ID<Group>, entity: Group): Boolean {
        val existingIndex = groups.indexOfFirst { it.id == id }
        return if (existingIndex >= 0) {
            groups[existingIndex] = entity
            true
        } else {
            groups += entity
            false
        }
    }

    override suspend fun deleteById(id: ID<Group>): Boolean = groups.removeIf { it.id == id }

    override fun findAll(): Flow<Group> = groups.asFlow()

    override suspend fun findBySlug(slug: String): Group? = groups.find { it.slug == slug }

    override suspend fun existsBySlug(slug: String): Boolean = groups.any { it.slug == slug }

    override suspend fun page(request: PageRequest): Page<Group> {
        val fromIndex = (request.page - 1) * request.size
        val items = groups.drop(fromIndex).take(request.size)
        return Page(
            items = items,
            total = groups.size.toLong(),
            page = request.page,
            size = request.size
        )
    }
}
