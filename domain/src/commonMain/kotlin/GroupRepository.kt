package com.github.oursharecar

interface GroupRepository : PagedRepository<String, Group> {
    suspend fun findBySlug(slug: String): Group?
    suspend fun existsBySlug(slug: String): Boolean
}
