@file:OptIn(ExperimentalTime::class)

package com.github.oursharecar.mongo.group

import com.github.oursharecar.domain.common.Auditable
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
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
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
            val group: MongoGroup = sampleGroup()

            val id = repository.insert(group)

            val loaded = repository.findById(id).shouldNotBeNull()
            loaded shouldBe group
            repository.existsById(id).shouldBeTrue()
        }

        test("find by slug honors soft delete filter") {
            val group = sampleGroup()
            val id = repository.insert(group)

            repository.findBySlug(group.slug).shouldNotBeNull()
            repository.existsBySlug(group.slug).shouldBeTrue()

            repository.deleteById(id).shouldBeTrue()

            repository.findBySlug(group.slug).shouldBeNull()
            repository.existsBySlug(group.slug).shouldBeFalse()
        }

        test("upsert updates existing entity") {
            val group = sampleGroup()
            val id = repository.insert(group)

            val updated = group.copy(name = "Updated Name")
            repository.upsert(id, updated).shouldBeTrue()

            val reloaded = repository.findById(id).shouldNotBeNull()
            reloaded.name shouldBe "Updated Name"
        }

        test("delete by id marks entity and excludes from queries") {
            val group = sampleGroup()
            val id = repository.insert(group)

            repository.deleteById(id).shouldBeTrue()

            repository.findById(id).shouldBeNull()
            repository.existsById(id).shouldBeFalse()
            repository.findAll().toList().shouldBeEmpty()

            val auditDocument = database
                .getCollection<Document>(collectionName)
                .find(Filters.eq("_id", group.id))
                .firstOrNull()
                .shouldNotBeNull()
            val audit = auditDocument.get("audit", Document::class.java).shouldNotBeNull()
            audit["deleted_at"].shouldNotBeNull()
        }

        test("page returns most recent groups first") {
            val groups = listOf(
                sampleGroup(createdAt = instantAt(1_000)),
                sampleGroup(createdAt = instantAt(2_000)),
                sampleGroup(createdAt = instantAt(3_000))
            )
            val ids = groups.map { repository.insert(it) }

            val firstPage = repository.page(PageRequest(page = 1, size = 2))
            firstPage.total shouldBe 3
            firstPage.items.map { it.id } shouldBe ids.take(2)

            val secondPage = repository.page(PageRequest(page = 2, size = 2))
            secondPage.items.map { it.id } shouldBe ids.takeLast(1)
        }
    }

    private fun sampleGroup(
        index: Int = 1,
        name: String = "Group $index",
        slug: String = "$index-slug",
        createdAt: Instant = instantAt(0),
        updatedAt: Instant = createdAt
    ): MongoGroup {
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

        return MongoGroup(
            name = name,
            slug = slug,
            settings = settings,
            audit = audit
        )
    }

    private fun instantAt(epochMilli: Long): Instant =
        Instant.fromEpochMilliseconds(epochMilli)
}
