plugins {
    id("dev.slne.surf.api.gradle.paper-raw")
}

repositories {
    maven("https://jitpack.io")
}

dependencies {
    api(projects.surfSkillApi.surfSkillApiCommon)
    compileOnly("com.github.Heliosares:AuxProtect:1.3.1")
}
