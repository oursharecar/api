package com.github.oursharecar.server.resources

import com.github.oursharecar.domain.common.ID
import com.github.oursharecar.domain.group.Group
import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/groups")
class Groups {
    @Resource("{id}")
    data class Id(val parent: Groups = Groups(), val id: ID<Group>) {
        @Resource("members")
        data class Members(val group: Id) {
            @Resource("{memberId}")
            class Id
        }
    }
}