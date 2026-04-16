package dev.slne.surf.skill.microservice.handler

import dev.slne.surf.rabbitmq.api.handler.RabbitHandler
import dev.slne.surf.skill.core.common.rabbit.packet.request.FindExperiencesRequestPacket
import dev.slne.surf.skill.core.common.rabbit.packet.request.SaveExperienceRequestPacket
import dev.slne.surf.skill.core.common.rabbit.packet.response.ManySimpleExperienceResponsePacket
import dev.slne.surf.skill.microservice.repository.ExperienceRepository
import kotlinx.coroutines.launch

object SkillExperienceHandler {
    @RabbitHandler
    fun handleFindExperiencesPacket(packet: FindExperiencesRequestPacket) = packet.launch {
        packet.respond(
            ManySimpleExperienceResponsePacket(
                ExperienceRepository.findExperiences(
                    packet.playerUuid
                )
            )
        )
    }

    @RabbitHandler
    fun handleSaveExperiencePacket(packet: SaveExperienceRequestPacket) = packet.launch {
        packet.simpleExperiences.forEach {
            ExperienceRepository.saveExperience(
                packet.playerUuid,
                it
            )
        }
    }
}