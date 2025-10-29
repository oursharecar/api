@file:OptIn(ExperimentalTime::class)

package com.github.oursharecar.mongo.group

import com.github.oursharecar.domain.common.Auditable
import com.github.oursharecar.domain.common.ID
import com.github.oursharecar.domain.common.PageRequest
import com.github.oursharecar.domain.group.Group
import com.github.oursharecar.mongo.config.mongoCodecRegistry
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import de.bwaldvogel.mongo.MongoServer
import de.bwaldvogel.mongo.backend.memory.MemoryBackend
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.bson.Document
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class GroupMongoRepositoryTest : FunSpec() {
    private lateinit var server: MongoServer
    private lateinit var client: MongoClient
    private lateinit var database: MongoDatabase
    private lateinit var repository: GroupMongoRepository

    private val collectionName = "groups"

    init {
        beforeTest {
            server = MongoServer(MemoryBackend())
            val address = server.bind()
            val connectionString = "mongodb://${address.hostString}:${address.port}"

            val settings = MongoClientSettings.builder()
                .applyConnectionString(ConnectionString(connectionString))
                .codecRegistry(mongoCodecRegistry())
                .build()

            client = MongoClient.create(settings)
            database = client.getDatabase("test-db")
            repository = GroupMongoRepository(database, collectionName)
        }

        afterTest {
            client.close()
            server.shutdown()
        }

        test("insert and find by id returns persisted entity") {
            val group = sampleGroup(id = ID("group-1"))

            repository.insert(group)

            val loaded = repository.findById(group.id!!).shouldNotBeNull()
            loaded shouldBe group
            repository.existsById(group.id!!).shouldBeTrue()
        }

        test("find by slug honors soft delete filter") {
            val group = sampleGroup(id = ID("group-2"), slug = "slug-2")
            repository.insert(group)

            repository.findBySlug(group.slug).shouldNotBeNull()
            repository.existsBySlug(group.slug).shouldBeTrue()

            repository.deleteById(group.id!!).shouldBeTrue()

            repository.findBySlug(group.slug).shouldBeNull()
            repository.existsBySlug(group.slug).shouldBeFalse()
        }

        test("upsert updates existing entity") {
            val group = sampleGroup(id = ID("group-3"), name = "Initial Name")
            repository.insert(group)

            val updated = group.copy(name = "Updated Name")
            repository.upsert(group.id!!, updated).shouldBeTrue()

            val reloaded = repository.findById(group.id!!).shouldNotBeNull()
            reloaded.name shouldBe "Updated Name"
        }

        test("delete by id marks entity and excludes from queries") {
            val group = sampleGroup(id = ID("group-4"))
            repository.insert(group)

            repository.deleteById(group.id!!).shouldBeTrue()

            repository.findById(group.id!!).shouldBeNull()
            repository.existsById(group.id!!).shouldBeFalse()
            repository.findAll().toList().shouldBeEmpty()

            val auditDocument = database
                .getCollection<Document>(collectionName)
                .find(Filters.eq("_id", group.id))
                .firstOrNull()
                .shouldNotBeNull()
            val audit = auditDocument.get("audit", Document::class.java).shouldNotBeNull()
            audit.get("deleted_at").shouldNotBeNull()
        }

        test("page returns most recent groups first") {
            val groups = listOf(
                sampleGroup(id = ID("group-5"), createdAt = instantAt(1_000)),
                sampleGroup(id = ID("group-6"), createdAt = instantAt(2_000)),
                sampleGroup(id = ID("group-7"), createdAt = instantAt(3_000))
            )
            groups.forEach { repository.insert(it) }

            val firstPage = repository.page(PageRequest(page = 1, size = 2))
            firstPage.total shouldBe 3
            firstPage.items.map { it.id } shouldBe listOf(ID("group-7"), ID("group-6"))

            val secondPage = repository.page(PageRequest(page = 2, size = 2))
            secondPage.items.map { it.id } shouldBe listOf(ID("group-5"))
        }
    }

    private fun sampleGroup(
        id: ID<Group>,
        name: String = "Group $id",
        slug: String = "$id-slug",
        createdAt: Instant = instantAt(0),
        updatedAt: Instant = createdAt
    ): Group {
        val audit = Auditable(
            createdAt = createdAt,
            createdBy = "system",
            updatedAt = updatedAt,
            updatedBy = "system"
        )

        val settings = Group.Settings(
            visibility = Group.Visibility.PUBLIC,
            joinMode = Group.JoinMode.OPEN,
            memberLimit = 10
        )

        return Group(
            id = id,
            name = name,
            slug = slug,
            settings = settings,
            audit = audit
        )
    }

    private fun instantAt(epochMilli: Long): Instant =
        Instant.fromEpochMilliseconds(epochMilli)
}
