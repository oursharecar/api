package com.github.oursharecar.domain.common

import kotlinx.serialization.modules.SerializersModule

val DomainSerializersModule: SerializersModule = SerializersModule {
    contextual(ID::class) { IDSerializer }
}
