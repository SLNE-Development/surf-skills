plugins {
    id("dev.slne.surf.api.gradle.paper-raw")
}

dependencies {
    api(projects.surfSkillCore)
}

surfRawPaperApi {
    withSurfDatabaseR2dbc("1.3.0", "dev.slne.surf.skill.libs.r2dbc")
}