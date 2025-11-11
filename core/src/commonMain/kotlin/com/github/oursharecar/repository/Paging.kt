package com.github.oursharecar.repository

/**
 * Represents a zero-based paging request with an optional sort definition.
 */
data class PageRequest(
    val page: Int,
    val size: Int,
    val sort: Sort = Sort.unsorted()
) {
    val offset: Long

    init {
        require(page >= 0) { "page must be >= 0" }
        require(size > 0) { "size must be > 0" }
        val computedOffset = page.toLong() * size
        require(computedOffset <= Int.MAX_VALUE) { "page * size must be <= Int.MAX_VALUE" }
        offset = computedOffset
    }
}

/**
 * Holds a slice of data returned for a paging request.
 */
data class Page<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val sort: Sort = Sort.unsorted()
) {
    init {
        require(page >= 0) { "page must be >= 0" }
        require(size > 0) { "size must be > 0" }
        require(totalElements >= 0) { "totalElements must be >= 0" }
    }

    val totalPages: Int = if (totalElements == 0L) 0 else ((totalElements - 1) / size + 1).toInt()
    val hasNext: Boolean = page + 1 < totalPages
    val hasPrevious: Boolean = page > 0
}
