package com.github.oursharecar.server.routes

import com.github.oursharecar.domain.group.GroupRepository
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.toList

fun Route.groupRoutes(groupRepository: GroupRepository) {
    route("/groups") {
        get {
            val groups = groupRepository.findAll().toList()
            call.respond(groups)
        }
    }
}
