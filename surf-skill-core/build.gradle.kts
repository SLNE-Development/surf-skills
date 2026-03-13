plugins {
    id("dev.slne.surf.surfapi.gradle.paper-raw")
}

dependencies {
    api(projects.surfSkillApi)
    compileOnlyApi(libs.surf.enchantment.api)
}