plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

application {
    mainClass = "io.ktor.server.cio.EngineMain"
}

dependencies {
    implementation(project(":core"))
    implementation(libs.ktor.server.rate.limiting)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.metrics.micrometer)
    implementation(libs.micrometer.registry.prometheus)
    implementation(libs.ktor.server.metrics)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.simple.cache)
    implementation(libs.ktor.simple.memory.cache)
    implementation(libs.ktor.server.default.headers)
    implementation(libs.ktor.server.conditional.headers)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.resources)
    implementation(libs.ktor.server.cio)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.slugify)
    implementation(libs.mongodb.driver.kotlin.coroutine)
    implementation(libs.mongodb.bson.kotlinx)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.core)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.assertions.ktor)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.mockk.mockk)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
