package com.github.oursharecar.server.service

import com.github.oursharecar.models.GroupResource
import com.github.oursharecar.models.ID
import com.github.oursharecar.models.UserResource
import com.github.oursharecar.repository.RepositoryFactory
import com.github.oursharecar.server.LinkedMapRepository
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
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ServerServiceImplTest : FunSpec({
    lateinit var groupRepository: LinkedMapRepository<GroupResource>
    lateinit var userRepository: LinkedMapRepository<UserResource>
    lateinit var service: ServerService

    beforeTest {
        groupRepository = LinkedMapRepository()
        service = ServerServiceImpl(
            object : RepositoryFactory {
                override fun getGroupRepository() = groupRepository
                override fun getUserRepository() = userRepository
            }
        )
    }

    test("getGroup returns persisted group when id exists") {
        val expected = sampleGroup(idValue = "group-7", name = "Neighborhood Carpool")
        groupRepository.seed("group-7", expected)

        service.getGroup(ID<GroupResource>("group-7")) shouldBe expected
    }

    test("getGroup returns null when group is missing") {
        service.getGroup(ID<GroupResource>("group-missing")) shouldBe null
    }

    test("listGroups emits all groups in insertion order") {
        val first = sampleGroup(idValue = "group-1", name = "Downtown Drivers")
        val second = sampleGroup(idValue = "group-2", name = "Weekend Riders")
        groupRepository.seed("group-1", first)
        groupRepository.seed("group-2", second)

        service.listGroups() shouldContainExactly listOf(first, second)
    }

    test("createGroup slugifies name and applies default settings") {
        val newId = service.createGroup(GroupCreateRequest(name = "Late Night Cruisers"))

        newId.id.shouldStartWith("group-")
        val stored = groupRepository.findById(newId).shouldNotBeNull()
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
        val group = sampleGroup(idValue = "group-3", name = "Sunrise Drivers")
        val id = groupRepository.seed("group-3", group)

        service.deleteGroup(id).shouldBeTrue()
        groupRepository.findById(id) shouldBe null
    }

    test("deleteGroup returns false when group does not exist") {
        service.deleteGroup(ID<GroupResource>("group-404")).shouldBeFalse()
    }
})
