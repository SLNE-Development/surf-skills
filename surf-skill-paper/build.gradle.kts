import dev.slne.surf.api.gradle.util.registerRequired
import dev.slne.surf.api.gradle.util.registerSoft
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("dev.slne.surf.api.gradle.paper-plugin")
}

dependencies {
    api(projects.surfSkillCore.surfSkillCorePaper)
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.skill.paper.PaperMain")
    foliaSupported(true)
    generateLibraryLoader(false)
    authors.addAll("Ammo", "red")

    serverDependencies {
        registerRequired(
            "surf-enchantment-paper",
            joinClassPath = true,
            loadOrder = PaperPluginDescription.RelativeLoadOrder.BEFORE
        )
        registerRequired("surf-rabbitmq-paper")
        registerSoft("surf-settings-paper")
        registerSoft("AuxProtect")
        registerSoft("surf-stats-paper")
    }
}
