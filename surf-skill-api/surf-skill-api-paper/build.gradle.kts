plugins {
    id("dev.slne.surf.api.gradle.paper-raw")
}

dependencies {
    api(projects.surfSkillApi.surfSkillApiCommon)
    compileOnly(files(rootProject.file("surf-skill-paper/libs/auxprotect-paper-1.3.4-pre6-all.jar")))
}
