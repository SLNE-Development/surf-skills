import dev.slne.surf.surfapi.gradle.util.registerRequired
import net.minecrell.pluginyml.paper.PaperPluginDescription

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

    serverDependencies {
        registerRequired(
            "surf-enchantment-paper",
            joinClassPath = true,
            loadOrder = PaperPluginDescription.RelativeLoadOrder.BEFORE
        )
    }
}
