package com.github.oursharecar.server.routes

import com.github.oursharecar.domain.common.Auditable
import com.github.oursharecar.domain.common.DomainSerializersModule
import com.github.oursharecar.domain.common.ID
import com.github.oursharecar.domain.common.Page
import com.github.oursharecar.domain.common.PageRequest
import com.github.oursharecar.domain.group.Group
import com.github.oursharecar.domain.group.GroupRepository
import com.github.oursharecar.server.models.GroupCreateRequest
import com.github.oursharecar.server.models.KtorSerializableGroup
import com.github.oursharecar.server.models.ktorSerializable
import com.github.oursharecar.server.plugins.configureRouting
import com.github.oursharecar.server.plugins.configureSerialization
import com.github.oursharecar.server.utils.GlobalSlugify
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.http.content.TextContent
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class GroupRoutesTest {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        serializersModule = DomainSerializersModule
    }

    @Test
    fun `GET groups returns all available groups`() = testApplication {
        val repository = FakeGroupRepository()
        val firstGroup = TestGroup(
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
        val secondGroup = TestGroup(
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
        val decoded = json.decodeFromString<List<KtorSerializableGroup>>(response.bodyAsText())
        assertEquals(
            listOf(firstGroup.ktorSerializable(), secondGroup.ktorSerializable()),
            decoded
        )
    }

    @Test
    fun `GET groups id returns existing group`() = testApplication {
        val repository = FakeGroupRepository()
        val group = TestGroup(
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
        val decoded = json.decodeFromString<KtorSerializableGroup>(response.bodyAsText())
        assertEquals(group.ktorSerializable(), decoded)
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

private data class TestGroup(
    override val name: String,
    override val slug: String,
    override val settings: Group.Settings,
    override val audit: Auditable
) : Group

private class FakeGroupRepository : GroupRepository<Group> {
    private val storage = linkedMapOf<ID<Group>, Group>()
    private val slugIndex = mutableMapOf<String, ID<Group>>()
    private var nextId = 1

    fun seed(idValue: String, group: Group): ID<Group> {
        val id = ID<Group>(idValue)
        storage[id] = group
        slugIndex[group.slug] = id
        updateCounter(idValue)
        return id
    }

    override suspend fun findById(id: ID<Group>): Group? = storage[id]

    override suspend fun existsById(id: ID<Group>): Boolean = storage.containsKey(id)

    override suspend fun insert(entity: Group): ID<Group> {
        var id: ID<Group>
        do {
            val idValue = "group-${nextId++}"
            id = ID<Group>(idValue)
        } while (storage.containsKey(id))

        storage[id] = entity
        slugIndex[entity.slug] = id
        return id
    }

    override suspend fun upsert(id: ID<Group>, entity: Group): Boolean {
        storage[id] = entity
        slugIndex[entity.slug] = id
        return true
    }

    override suspend fun deleteById(id: ID<Group>): Boolean = storage.remove(id) != null

    override fun findAll(): Flow<Group> = flow {
        storage.values.forEach { emit(it) }
    }

    override suspend fun page(request: PageRequest): Page<Group> {
        val items = storage.values.toList()
        val fromIndex = ((request.page - 1) * request.size).coerceAtLeast(0)
        val toIndex = (fromIndex + request.size).coerceAtMost(items.size)
        val pagedItems = if (fromIndex >= items.size) emptyList() else items.subList(fromIndex, toIndex)

        return Page(
            items = pagedItems,
            total = items.size.toLong(),
            page = request.page,
            size = request.size
        )
    }

    override suspend fun findBySlug(slug: String): Group? = slugIndex[slug]?.let { storage[it] }

    override suspend fun existsBySlug(slug: String): Boolean = slugIndex.containsKey(slug)

    private fun updateCounter(idValue: String) {
        val numericSuffix = idValue.substringAfterLast('-', "")
        val candidate = numericSuffix.toIntOrNull()?.plus(1) ?: return
        if (candidate > nextId) {
            nextId = candidate
        }
    }
}
