package com.github.oursharecar.server.resources

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/groups")
class Groups {
    @Serializable
    @Resource("{id}")
    data class Id(val id: String) {
        @Resource("members")
        class Members(val group: Id) {
            @Resource("{memberId}")
            class Id
        }
    }
}