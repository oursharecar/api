package com.github.oursharecar.server.routes

import com.github.oursharecar.models.GroupResource
import com.github.oursharecar.repository.Repository
import com.github.oursharecar.server.models.GroupCreateRequest
import com.github.oursharecar.server.models.buildResource
import com.github.oursharecar.server.resources.Groups
import com.github.oursharecar.server.utils.GlobalSlugify
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.toList

fun Route.groupRoutes(groupRepository: Repository<GroupResource>) {
    get<Groups> {
        val groups = groupRepository.findAll().toList()
        call.respond(groups)
    }
    post<Groups> {
        val request = call.receive<GroupCreateRequest>()
        val sub = "sub(placeholder)"
        val slug = GlobalSlugify.slugify(request.name)
        val id = groupRepository.insert(request.buildResource(sub, slug))
        call.respond(id.id)
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
