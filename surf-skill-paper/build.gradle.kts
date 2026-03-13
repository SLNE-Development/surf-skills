plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

dependencies {
    api(projects.surfSkillCore)
    runtimeOnly(projects.surfSkillBackend)
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.skill.paper.PaperMain")
    foliaSupported(true)
    generateLibraryLoader(false)
    authors.add("Ammo")
}
