import dev.slne.surf.microservice.gradle.plugin.rabbit.RabbitModule

plugins {
    id("dev.slne.surf.api.gradle.paper-raw")
    id("dev.slne.surf.microservice")
}

surfMicroservice {
    withRabbitModule(RabbitModule.CLIENT_API)
}

surfRawPaperApi {
    withCorePaper()
}

dependencies {
    api(projects.surfSkillCore.surfSkillCoreCommon)
    api(projects.surfSkillApi.surfSkillApiPaper)
    compileOnly(libs.surf.enchantment.api)
    compileOnlyApi("dev.slne.surf.settings:surf-settings-api:+")
    compileOnlyApi("dev.slne.surf.stats:surf-stats-api:+")
}