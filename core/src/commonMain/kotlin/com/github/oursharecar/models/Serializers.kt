package com.github.oursharecar.models

import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.serializers.FormattedInstantSerializer

object Rfc1123Serializer : FormattedInstantSerializer(
    "com.github.oursharecar.server.models.Rfc1123Serializer",
    DateTimeComponents.Formats.RFC_1123
)