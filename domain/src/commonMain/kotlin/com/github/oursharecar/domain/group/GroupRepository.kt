package com.github.oursharecar.domain.group

import com.github.oursharecar.domain.common.PagedRepository

interface GroupRepository : PagedRepository<String, Group> {
    suspend fun findBySlug(slug: String): Group?
    suspend fun existsBySlug(slug: String): Boolean
}
