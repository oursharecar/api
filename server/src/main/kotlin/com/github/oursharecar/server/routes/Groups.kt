package com.github.oursharecar.server.routes

import com.github.oursharecar.models.GroupResource
import com.github.oursharecar.models.ID
import com.github.oursharecar.server.service.ServerService
import io.github.westelh.ktor.simpleCache.cacheOutput
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.request.*
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.seconds

private val groupsCollectionCacheTtl = 5.seconds
private val groupDetailCacheTtl = 30.seconds

@Serializable
@Resource("/groups")
class Groups {
    @Resource("{id}")
    data class Id(val parent: Groups = Groups(), val id: ID<GroupResource>) {
        @Resource("members")
        data class Members(val group: Id) {
            @Resource("{memberId}")
            class Id
        }
    }
}

fun Route.groupRoutes(service: ServerService) {
    // Operations on a collection of groups
    cacheOutput(invalidateAt = groupsCollectionCacheTtl) {
        get<Groups> {
            call.respond<List<GroupResource>>(service.listGroups())
        }
    }
    post<Groups> {
        call.respond<ID<GroupResource>>(service.createGroup(call.receive()))
    }

    // Operations on a single group identified by ID
    cacheOutput(invalidateAt = groupDetailCacheTtl) {
        get<Groups.Id> { group ->
            val result = service.getGroup(group.id)
            if (result != null) {
                call.respond<GroupResource>(result)
            } else {
                call.respond<String>(HttpStatusCode.NotFound, "Group not found")
            }
        }
    }
    patch<Groups.Id> { group ->
        // TODO: Implement updating a group by ID with authentication
        call.respond(HttpStatusCode.NotImplemented)
    }
    delete<Groups.Id> { group ->
        coroutineScope {
            async {
                service.deleteGroup(group.id)
            }
        }
        call.respond(HttpStatusCode.NoContent)
    }

    // Operations on group members
    get<Groups.Id.Members> { groupMembers ->
        // TODO: Implement getting group members by group ID with authentication
        call.respond(HttpStatusCode.NotImplemented)
    }
    post<Groups.Id.Members> { groupMembers ->
        // TODO: Implement adding a group member with authentication
        call.respond(HttpStatusCode.NotImplemented)
    }

    // Operations on a single group member identified by ID
    patch<Groups.Id.Members.Id> { groupMember ->
        // TODO: Implement getting a group member by ID with authentication
        call.respond(HttpStatusCode.NotImplemented)
    }
    delete<Groups.Id.Members.Id> { groupMember ->
        // TODO: Implement removing a group member by ID with authentication
        call.respond(HttpStatusCode.NotImplemented)
    }
}
