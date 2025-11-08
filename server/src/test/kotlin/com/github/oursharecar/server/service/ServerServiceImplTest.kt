package com.github.oursharecar.server.service

import com.github.oursharecar.models.GroupResource
import com.github.oursharecar.models.ID
import com.github.oursharecar.repository.Repository
import com.github.oursharecar.repository.RepositoryFactory
import com.github.oursharecar.server.fixtures.sampleGroup
import com.github.oursharecar.server.models.GroupCreateRequest
import com.github.oursharecar.server.utils.GlobalSlugify
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ServerServiceImplTest : FunSpec({
    test("getGroup returns persisted group when id exists") {
        val (service, groupRepository) = buildService()
        val id = ID<GroupResource>("group-7")
        val expected = sampleGroup(idValue = "group-7", name = "Neighborhood Carpool")
        coEvery { groupRepository.findById(id) } returns expected

        service.getGroup(id) shouldBe expected
        coVerify(exactly = 1) { groupRepository.findById(id) }
    }

    test("getGroup returns null when group is missing") {
        val (service, groupRepository) = buildService()
        val id = ID<GroupResource>("group-missing")
        coEvery { groupRepository.findById(id) } returns null

        service.getGroup(id) shouldBe null
        coVerify(exactly = 1) { groupRepository.findById(id) }
    }

    test("listGroups emits all groups in insertion order") {
        val (service, groupRepository) = buildService()
        val first = sampleGroup(idValue = "group-1", name = "Downtown Drivers")
        val second = sampleGroup(idValue = "group-2", name = "Weekend Riders")
        every { groupRepository.findAll() } returns flowOf(first, second)

        service.listGroups() shouldContainExactly listOf(first, second)
        verify(exactly = 1) { groupRepository.findAll() }
    }

    test("createGroup slugifies name and applies default settings") {
        val (service, groupRepository) = buildService()
        val capturedResource = slot<GroupResource>()
        coEvery { groupRepository.insert(capture(capturedResource)) } returns ID("group-123")

        val newId = service.createGroup(GroupCreateRequest(name = "Late Night Cruisers"))

        newId.id.shouldStartWith("group-")
        coVerify(exactly = 1) { groupRepository.insert(any()) }
        val stored = capturedResource.captured.shouldNotBeNull()
        stored.name shouldBe "Late Night Cruisers"
        stored.slug shouldBe GlobalSlugify.slugify("Late Night Cruisers")
        stored.settings shouldBe GroupResource.Settings(
            visibility = GroupResource.Visibility.PRIVATE,
            joinMode = GroupResource.JoinMode.INVITE,
            memberLimit = 10
        )
        stored.audit.createdBy shouldBe "sub(placeholder)"
        stored.audit.updatedBy shouldBe "sub(placeholder)"
    }

    test("deleteGroup removes existing group") {
        val (service, groupRepository) = buildService()
        val id = ID<GroupResource>("group-3")
        coEvery { groupRepository.deleteById(id) } returns true

        service.deleteGroup(id).shouldBeTrue()
        coVerify(exactly = 1) { groupRepository.deleteById(id) }
    }

    test("deleteGroup returns false when group does not exist") {
        val (service, groupRepository) = buildService()
        val id = ID<GroupResource>("group-404")
        coEvery { groupRepository.deleteById(id) } returns false

        service.deleteGroup(id).shouldBeFalse()
        coVerify(exactly = 1) { groupRepository.deleteById(id) }
    }
})

private fun buildService(): Pair<ServerService, Repository<GroupResource>> {
    val groupRepository = mockk<Repository<GroupResource>>(relaxed = true)
    val factory = mockk<RepositoryFactory> {
        every { getGroupRepository() } returns groupRepository
        every { getUserRepository() } returns mockk()
    }
    return ServerServiceImpl(factory) to groupRepository
}
