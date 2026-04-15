plugins {
    id("dev.slne.surf.api.gradle.paper-raw")
}

dependencies {
    api(projects.surfSkillApi)
    compileOnlyApi(libs.surf.enchantment.api)
}