package com.github.oursharecar.server.models

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class ID<E>(val id: String)

