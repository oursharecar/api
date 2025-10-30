package com.github.oursharecar.domain.group

import com.github.oursharecar.domain.common.PagedRepository

interface GroupRepository<E : Group> : PagedRepository<E> {
    suspend fun findBySlug(slug: String): E?
    suspend fun existsBySlug(slug: String): Boolean
}
