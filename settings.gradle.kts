rootProject.name = "surf-skills"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://reposilite.slne.dev/releases")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.slne.surf.api.gradle.settings") version "+"
}

include("surf-skill-api")
include("surf-skill-core")
include("surf-skill-microservice")
include("surf-skill-paper")
include("surf-skill-core:surf-skill-core-common")
include("surf-skill-core:surf-skill-core-paper")