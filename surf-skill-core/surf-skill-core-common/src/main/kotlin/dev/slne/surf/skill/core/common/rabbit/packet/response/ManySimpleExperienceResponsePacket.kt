package dev.slne.surf.skill.core.common.rabbit.packet.response

import dev.slne.surf.rabbitmq.api.packet.RabbitResponsePacket
import dev.slne.surf.skill.api.common.experience.SimpleExperience
import kotlinx.serialization.Serializable

@Serializable
data class ManySimpleExperienceResponsePacket(
    val simpleExperiences: List<SimpleExperience>
) : RabbitResponsePacket()