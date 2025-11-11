package com.github.oursharecar.repository

/**
 * Specification pattern for repository filtering.
 */
fun interface Specification<T> {
    fun isSatisfiedBy(candidate: T): Boolean

    fun and(other: Specification<T>): Specification<T> = Specification { candidate ->
        isSatisfiedBy(candidate) && other.isSatisfiedBy(candidate)
    }

    fun or(other: Specification<T>): Specification<T> = Specification { candidate ->
        isSatisfiedBy(candidate) || other.isSatisfiedBy(candidate)
    }

    fun not(): Specification<T> = Specification { candidate -> !isSatisfiedBy(candidate) }

    companion object {
        fun <T> alwaysTrue(): Specification<T> = Specification { true }
        fun <T> alwaysFalse(): Specification<T> = Specification { false }
    }
}
