plugins {
    alias(libs.plugins.kotlin.multiplatform)
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
            implementation(libs.mongodb.driver.kotlin.coroutine)
            implementation(libs.mongodb.bson.kotlinx)
        }
        jvmTest.dependencies {
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotest.runner.junit5)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.mongo.java.server)
        }
    }
}

tasks.withType<org.gradle.api.tasks.testing.Test>().configureEach {
    useJUnitPlatform()
}
