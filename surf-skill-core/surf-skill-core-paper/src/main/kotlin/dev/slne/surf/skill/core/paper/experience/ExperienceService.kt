package dev.slne.surf.skill.core.paper.experience

import dev.slne.surf.api.core.util.toObjectList
import dev.slne.surf.skill.api.paper.experience.SkillExperience
import dev.slne.surf.skill.core.common.rabbit.packet.request.FindExperiencesRequestPacket
import dev.slne.surf.skill.core.common.rabbit.packet.request.SaveExperienceRequestPacket
import dev.slne.surf.skill.core.paper.PaperSkillInstance
import it.unimi.dsi.fastutil.objects.ObjectList
import java.util.*

object ExperienceService {
    suspend fun fetchPlayerExperience(uuid: UUID): ObjectList<SkillExperience> =
        PaperSkillInstance.rabbitApi.sendRequest(
            FindExperiencesRequestPacket(uuid)
        ).simpleExperiences.map {
            it.experience()
        }.toObjectList()

    suspend fun savePlayerExperience(
        uuid: UUID,
        experience: ObjectList<SkillExperience>
    ) = PaperSkillInstance.rabbitApi.sendRequest(
        SaveExperienceRequestPacket(
            playerUuid = uuid,
            simpleExperiences = experience.map { it.simple() }.toObjectList()
        )
    )
}