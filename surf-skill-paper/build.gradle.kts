plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

dependencies {
    api(project(":surf-skill-core"))
    api(project(":surf-skill-backend"))
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.skill.paper.PaperMain")
    foliaSupported(true)
    generateLibraryLoader(false)
    authors.add("Ammo")
}
