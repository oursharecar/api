plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.plugin.serialization)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            api(libs.opentelemetry.sdk.extension.autoconfigure)
            api(libs.opentelemetry.semconv)
            api(libs.opentelemetry.exporter.otlp)
            api(libs.opentelemetry.ktor)
            api(project(":domain"))
            api(libs.mongodb.driver.kotlin.coroutine)
            api(libs.mongodb.bson.kotlinx)
        }
        jvmTest.dependencies {
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotest.runner.junit5)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.mongo.java.server)
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
