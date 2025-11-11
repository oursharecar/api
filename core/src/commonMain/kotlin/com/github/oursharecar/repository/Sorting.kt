package com.github.oursharecar.repository

/**
 * Direction for field level ordering.
 */
enum class SortDirection {
    ASC,
    DESC
}

/**
 * Field-specific sort instruction.
 */
data class SortField(
    val property: String,
    val direction: SortDirection = SortDirection.ASC
)

/**
 * Collection of sort instructions, maintains declaration order.
 */
data class Sort(
    val fields: List<SortField>
) {
    companion object {
        fun unsorted(): Sort = Sort(emptyList())
        fun by(property: String, direction: SortDirection = SortDirection.ASC): Sort =
            Sort(listOf(SortField(property, direction)))
    }

    val isSorted: Boolean = fields.isNotEmpty()

    fun and(property: String, direction: SortDirection = SortDirection.ASC): Sort =
        Sort(fields + SortField(property, direction))
}
