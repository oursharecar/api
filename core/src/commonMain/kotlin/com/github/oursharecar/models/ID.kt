package com.github.oursharecar.models

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class ID<E>(val id: String)

