package dev.slne.surf.skill.core.paper

import dev.slne.surf.rabbitmq.api.ClientRabbitMQApi
import java.nio.file.Path

class PaperLoader(
    dataPath: Path
) {
    val rabbitApi = ClientRabbitMQApi.create("surf-skills", dataPath)

    suspend fun onLoad() {
        rabbitApi.freezeAndConnect()
    }

    suspend fun onDisable() {
        rabbitApi.disconnect()
    }
}