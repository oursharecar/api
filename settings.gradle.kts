rootProject.name = "api"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://packages.confluent.io/maven/")
    }
}

include(":server")
include(":core")