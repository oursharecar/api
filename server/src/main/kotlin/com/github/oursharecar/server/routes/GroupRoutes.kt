package com.github.oursharecar.server.routes

import com.github.oursharecar.domain.group.GroupRepository
import com.github.oursharecar.server.plugins.groups
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.toList

fun Route.groupRoutes(groupRepository: GroupRepository) {
    route("/groups") {
        get {
            val groups = groupRepository.findAll().toList()
            call.respond(groups)
        }
        post {
            // TODO: Implement group creation
            call.respond(HttpStatusCode.NotImplemented)
        }

        authenticate("jwt-groups-claims") {
            get("/{id}") {
                if (!call.isMemberOfGroup(call.parameters["id"])) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@get
                }
                // TODO: Implement get group by ID
                call.respond(HttpStatusCode.NotImplemented)
            }
            patch("/{id}") {
                // TODO: Implement group update
                call.respond(HttpStatusCode.NotImplemented)
            }
            delete("/{id}") {
                // TODO: Implement group deletion
                call.respond(HttpStatusCode.NotImplemented)
            }
            get("/{id}/members") {
                // TODO: Implement get group members
                call.respond(HttpStatusCode.NotImplemented)
            }
            post("/{id}/members") {
                // TODO: Implement add group member
                call.respond(HttpStatusCode.NotImplemented)
            }
            patch("/{id}/members/{memberId}") {
                // TODO: Implement update group member
                call.respond(HttpStatusCode.NotImplemented)
            }
            delete("/{id}/members/{memberId}") {
                // TODO: Implement remove group member
                call.respond(HttpStatusCode.NotImplemented)
            }
        }
    }
}

fun ApplicationCall.isMemberOfGroup(groupId: String?): Boolean =
    principal<JWTPrincipal>()?.payload?.groups?.contains(groupId) == true
