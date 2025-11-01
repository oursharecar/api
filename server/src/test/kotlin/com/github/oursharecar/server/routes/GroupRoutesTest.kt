package com.github.oursharecar.server.routes

import com.github.oursharecar.models.Auditable
import com.github.oursharecar.models.Group
import com.github.oursharecar.models.ID
import com.github.oursharecar.repository.Repository
import com.github.oursharecar.server.models.GroupCreateRequest
import com.github.oursharecar.server.plugins.configureRouting
import com.github.oursharecar.server.plugins.configureSerialization
import com.github.oursharecar.server.utils.GlobalSlugify
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.testing.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
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
        val repository = FakeGroupRepository()
        val firstGroup = Group(
            id = null,
            name = "Downtown Drivers",
            slug = "downtown-drivers",
            settings = Group.Settings(
                visibility = Group.Visibility.PUBLIC,
                joinMode = Group.JoinMode.OPEN,
                memberLimit = 50
            ),
            audit = audit(
                createdBy = "alice",
                updatedBy = "alice",
                createdAtMillis = 1_000L,
                updatedAtMillis = 2_000L
            )
        )
        val secondGroup = Group(
            id = null,
            name = "Weekend Riders",
            slug = "weekend-riders",
            settings = Group.Settings(
                visibility = Group.Visibility.PRIVATE,
                joinMode = Group.JoinMode.INVITE,
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
        val decoded = json.decodeFromString<List<Group>>(response.bodyAsText())
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
        val repository = FakeGroupRepository()
        val group = Group(
            id = null,
            name = "Neighborhood Carpool",
            slug = "neighborhood-carpool",
            settings = Group.Settings(
                visibility = Group.Visibility.PUBLIC,
                joinMode = Group.JoinMode.REQUEST,
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
        val decoded = json.decodeFromString<Group>(response.bodyAsText())
        assertEquals(group.copy(id = ID("group-42")), decoded)
    }

    @Test
    fun `GET groups id returns 404 for missing group`() = testApplication {
        val repository = FakeGroupRepository()

        application {
            configureSerialization()
            configureRouting(repository)
        }

        val response = client.get("/groups/group-missing")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `POST groups persists group and returns new id`() = testApplication {
        val repository = FakeGroupRepository()

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
        val createdId = response.bodyAsText()
        assertTrue(createdId.startsWith("group-"))

        val stored = runBlocking { repository.findById(ID<Group>(createdId)) }
        assertNotNull(stored)
        assertEquals("Late Night Cruisers", stored.name)
        assertEquals(GlobalSlugify.slugify("Late Night Cruisers"), stored.slug)
    }

    private fun audit(
        createdBy: String,
        updatedBy: String,
        createdAtMillis: Long,
        updatedAtMillis: Long
    ) = Auditable(
        createdAt = Instant.fromEpochMilliseconds(createdAtMillis),
        createdBy = createdBy,
        updatedAt = Instant.fromEpochMilliseconds(updatedAtMillis),
        updatedBy = updatedBy
    )
}

private class FakeGroupRepository : Repository<Group> {
    private val storage = linkedMapOf<ID<Group>, Group>()
    private val slugIndex = mutableMapOf<String, ID<Group>>()
    private var nextId = 1

    fun seed(idValue: String, group: Group): ID<Group> {
        val id = ID<Group>(idValue)
        val resource = group.copy(id = id)
        storage[id] = resource
        slugIndex[resource.slug] = id
        updateCounter(idValue)
        return id
    }

    override suspend fun findById(id: ID<Group>): Group? = storage[id]

    override suspend fun existsById(id: ID<Group>): Boolean = storage.containsKey(id)
    override suspend fun findBySlug(slug: String): Group? {
        TODO("Not yet implemented")
    }

    override suspend fun existsBySlug(slug: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun insert(entity: Group): ID<Group> {
        var id: ID<Group>
        do {
            val idValue = "group-${nextId++}"
            id = ID(idValue)
        } while (storage.containsKey(id))

        val resource = entity.copy(id = id)
        storage[id] = resource
        slugIndex[resource.slug] = id
        return id
    }

    override suspend fun deleteById(id: ID<Group>): Boolean = storage.remove(id) != null

    override fun findAll(): Flow<Group> = flow {
        storage.values.forEach { emit(it) }
    }

    private fun updateCounter(idValue: String) {
        val numericSuffix = idValue.substringAfterLast('-', "")
        val candidate = numericSuffix.toIntOrNull()?.plus(1) ?: return
        if (candidate > nextId) {
            nextId = candidate
        }
    }
}
