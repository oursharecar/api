package com.github.oursharecar.server.routes

import com.github.oursharecar.domain.group.GroupRepository
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
                withGroupMember {
                    // TODO: Implement get group by ID
                    call.respond(HttpStatusCode.NotImplemented)
                }
            }
            patch("/{id}") {
                withGroupMember {
                    // TODO: Implement group update
                    call.respond(HttpStatusCode.NotImplemented)
                }
            }
            delete("/{id}") {
                withGroupMember {
                    // TODO: Implement group deletion
                    call.respond(HttpStatusCode.NotImplemented)
                }
            }
            get("/{id}/members") {
                withGroupMember {
                    // TODO: Implement get group members
                    call.respond(HttpStatusCode.NotImplemented)
                }
            }
            post("/{id}/members") {
                withGroupMember {
                    // TODO: Implement add group member
                    call.respond(HttpStatusCode.NotImplemented)
                }
            }
            patch("/{id}/members/{memberId}") {
                withGroupMember {
                    // TODO: Implement update group member
                    call.respond(HttpStatusCode.NotImplemented)
                }
            }
            delete("/{id}/members/{memberId}") {
                withGroupMember {
                    // TODO: Implement remove group member
                    call.respond(HttpStatusCode.NotImplemented)
                }
            }
        }
    }
}

fun ApplicationCall.isMemberOfGroup(groupId: String?): Boolean =
    principal<JWTPrincipal>()?.payload?.getClaim("groups")?.asList(String::class.java)?.contains(groupId) == true

private suspend inline fun RoutingContext.withGroupMember(
    block: suspend (String) -> Unit
) {
    val groupId = call.parameters["id"]
    if (groupId == null || !call.isMemberOfGroup(groupId)) {
        call.respond(HttpStatusCode.Unauthorized)
        return
    }
    block(groupId)
}
