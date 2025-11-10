plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.kover)
}

application {
    mainClass = "io.ktor.server.cio.EngineMain"
}

dependencies {
    implementation(project(":core"))

    // Ktor server and HTTP features
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.conditional.headers)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.default.headers)
    implementation(libs.ktor.server.rate.limiting)
    implementation(libs.ktor.server.resources)
    implementation(libs.ktor.server.status.pages)

    // Serialization and data formats
    implementation(libs.kotlinx.datetime)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.core)

    // Caching
    implementation(libs.ktor.simple.cache)
    implementation(libs.ktor.simple.memory.cache)

    // Observability and configuration
    implementation(libs.ktor.server.metrics.micrometer)
    implementation(libs.micrometer.registry.prometheus)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.logback.classic)

    // Data access and utilities
    implementation(libs.slugify)

    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.assertions.ktor)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.mockk.mockk)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
