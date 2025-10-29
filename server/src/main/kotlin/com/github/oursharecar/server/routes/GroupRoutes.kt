package com.github.oursharecar.server.routes

import com.github.oursharecar.domain.group.GroupRepository
import io.ktor.http.*
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.Serializable

@Serializable
data class CreateGroupRequest(
    val name: String,
    val members: List<String> = emptyList()
)

@Serializable
data class Group(
    val id: String,
    val name: String,
    val members: List<String> = emptyList()
)

fun Route.groupRoutes(groupRepository: GroupRepository) {
    get<Groups> {
        val groups = groupRepository.findAll().toList()
        call.respond(groups)
    }
    post<Groups> {
        val request = call.receive<CreateGroupRequest>()
        call.respond(request)
    }

    get<Groups.Id> { group ->
        val result = groupRepository.findById(group.id)
        if (result != null) {
            call.respond(result)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
    patch<Groups.Id> { group ->
        // TODO: Implement updating a group by ID with authentication
        call.respond(HttpStatusCode.NotImplemented)
    }
    post<Groups.Id> { group ->
        // TODO: Implement deleting a group by ID with authentication
        call.respond(HttpStatusCode.NotImplemented)
    }
    patch<Groups.Id> { group ->
        // TODO: Implement adding a member to a group by ID with authentication
        call.respond(HttpStatusCode.NotImplemented)
    }
    delete<Groups.Id> { group ->
        // TODO: Implement removing a member from a group by ID with authentication
        call.respond(HttpStatusCode.NotImplemented)
    }

    get<Groups.Id.Members> { groupMembers ->
        // TODO: Implement getting group members by group ID with authentication
        call.respond(HttpStatusCode.NotImplemented)
    }
    post<Groups.Id.Members> { groupMembers ->
        // TODO: Implement adding a group member with authentication
        call.respond(HttpStatusCode.NotImplemented)
    }

    patch<Groups.Id.Members.Id> { groupMember ->
        // TODO: Implement getting a group member by ID with authentication
        call.respond(HttpStatusCode.NotImplemented)
    }
    delete<Groups.Id.Members.Id> { groupMember ->
        // TODO: Implement removing a group member by ID with authentication
        call.respond(HttpStatusCode.NotImplemented)
    }
}

fun ApplicationCall.isMemberOfGroup(groupId: String?): Boolean =
    principal<JWTPrincipal>()?.payload?.getClaim("groups")?.asList(String::class.java)?.contains(groupId) == true

suspend fun RoutingContext.checkGroupClaimMatchesParameter(
    key: String,
    block: suspend RoutingContext.(String) -> Unit
) {
    val groupId = call.parameters.getOrFail(key)
    if (!call.isMemberOfGroup(groupId)) {
        call.respond(HttpStatusCode.Unauthorized)
        return
    }
    block(groupId)
}
