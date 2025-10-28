package com.github.oursharecar.telemetry

import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk
import io.opentelemetry.semconv.ServiceAttributes

fun buildOpenTelemetry(serviceName: String): OpenTelemetry {
    // Disable metrics exporter because the `jaegertracing/all-in-one` image we use locally
    // does not support OpenTelemetry metrics. This avoids unnecessary configuration warnings.
    System.setProperty("otel.metrics.exporter", "none")

    return AutoConfiguredOpenTelemetrySdk.builder().addResourceCustomizer { oldResource, _ ->
        oldResource.toBuilder()
            .putAll(oldResource.attributes)
            .put(ServiceAttributes.SERVICE_NAME, serviceName)
            .build()
    }.build().openTelemetrySdk
}
