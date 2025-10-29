package com.github.oursharecar.server.routes

import com.github.oursharecar.domain.group.GroupRepository
import io.ktor.server.auth.*
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
            call.respondText("Group creation not implemented", status = io.ktor.http.HttpStatusCode.NotImplemented)
        }

        authenticate("jwt-sub-claims") {
            get("/{id}") {
                // TODO: Implement get group by ID
                call.respondText("Get group by ID not implemented", status = io.ktor.http.HttpStatusCode.NotImplemented)
            }
            patch("/{id}") {
                // TODO: Implement group update
                call.respondText("Group update not implemented", status = io.ktor.http.HttpStatusCode.NotImplemented)
            }
            delete("/{id}") {
                // TODO: Implement group deletion
                call.respondText("Group deletion not implemented", status = io.ktor.http.HttpStatusCode.NotImplemented)
            }
            get("/{id}/members") {
                // TODO: Implement get group members
                call.respondText(
                    "Get group members not implemented",
                    status = io.ktor.http.HttpStatusCode.NotImplemented
                )
            }
            post("/{id}/members") {
                // TODO: Implement add group member
                call.respondText(
                    "Add group member not implemented",
                    status = io.ktor.http.HttpStatusCode.NotImplemented
                )
            }
            patch("/{id}/members/{memberId}") {
                // TODO: Implement update group member
                call.respondText(
                    "Update group member not implemented",
                    status = io.ktor.http.HttpStatusCode.NotImplemented
                )
            }
            delete("/{id}/members/{memberId}") {
                // TODO: Implement remove group member
                call.respondText(
                    "Remove group member not implemented",
                    status = io.ktor.http.HttpStatusCode.NotImplemented
                )
            }
        }
    }
}