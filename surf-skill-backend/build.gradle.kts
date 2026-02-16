plugins {
    id("dev.slne.surf.surfapi.gradle.paper-raw")
}

dependencies {
    api(project(":surf-skill-core"))
}

surfRawPaperApi {
    withSurfDatabaseR2dbc("1.3.0", "dev.slne.surf.skill.libs.r2dbc")
}