package com.github.oursharecar.models

/**
 * Contract for resources that expose their ID and can produce copies with a new ID.
 */
interface Identifiable<E> {
    val id: ID<E>?

    fun withId(id: ID<E>): E
}
