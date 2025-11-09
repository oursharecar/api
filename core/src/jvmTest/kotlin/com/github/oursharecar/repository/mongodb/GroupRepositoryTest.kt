package com.github.oursharecar.repository.mongodb

import com.github.oursharecar.models.AuditableResource
import com.github.oursharecar.models.GroupResource
import com.github.oursharecar.models.ID
import com.github.oursharecar.repository.Repository
import com.github.oursharecar.repository.mongodb.documents.AuditableDocument
import com.github.oursharecar.repository.mongodb.documents.GroupDocument
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class GroupRepositoryTest : FunSpec({
    lateinit var impl: Repository<GroupDocument>
    lateinit var repository: GroupRepository

    val defaultSettings = GroupResource.Settings(
        visibility = GroupResource.Visibility.PRIVATE,
        joinMode = GroupResource.JoinMode.REQUEST,
        memberLimit = 32
    )

    val auditDocument = AuditableDocument(
        createdAt = Instant.fromEpochSeconds(1),
        createdBy = "creator",
        updatedAt = Instant.fromEpochSeconds(2),
        updatedBy = "updater"
    )

    val auditResource = AuditableResource(
        createdAt = auditDocument.createdAt,
        createdBy = auditDocument.createdBy,
        updatedAt = auditDocument.updatedAt,
        updatedBy = auditDocument.updatedBy
    )

    beforeTest {
        impl = mockk()
        repository = GroupRepository(impl)
    }

    test("findById maps the document returned by the wrapped repository") {
        val objectId = ObjectId()
        val document = GroupDocument(
            id = objectId,
            name = "Fleet",
            slug = "fleet",
            settings = defaultSettings,
            audit = auditDocument
        )
        val expected = GroupResource(
            id = ID(objectId.toHexString()),
            name = document.name,
            slug = document.slug,
            settings = defaultSettings,
            audit = auditResource
        )

        coEvery { impl.findById(ID<GroupDocument>(objectId.toHexString())) } returns document

        val result = repository.findById(ID(objectId.toHexString()))

        result shouldBe expected
        coVerify(exactly = 1) { impl.findById(ID<GroupDocument>(objectId.toHexString())) }
    }

    test("insert converts the resource to a document and returns the mapped id") {
        val newResource = GroupResource(
            id = null,
            name = "Alpha",
            slug = "alpha",
            settings = defaultSettings,
            audit = auditResource
        )
        val captured = slot<GroupDocument>()
        val generatedId = ObjectId()

        coEvery { impl.insert(capture(captured)) } returns ID(generatedId.toHexString())

        val result = repository.insert(newResource)

        captured.captured shouldBe GroupDocument(
            id = null,
            name = newResource.name,
            slug = newResource.slug,
            settings = defaultSettings,
            audit = auditDocument
        )
        result shouldBe ID<GroupResource>(generatedId.toHexString())
        coVerify(exactly = 1) { impl.insert(any()) }
    }

    test("findAll maps every document emitted by the wrapped repository") {
        val first = ObjectId()
        val second = ObjectId()

        every { impl.findAll() } returns flowOf(
            GroupDocument(first, "A", "a", defaultSettings, auditDocument),
            GroupDocument(second, "B", "b", defaultSettings, auditDocument)
        )

        val result = repository.findAll().toList()

        result.shouldContainExactly(
            GroupResource(ID(first.toHexString()), "A", "a", defaultSettings, auditResource),
            GroupResource(ID(second.toHexString()), "B", "b", defaultSettings, auditResource)
        )
        verify(exactly = 1) { impl.findAll() }
    }
})
