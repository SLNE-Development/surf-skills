package dev.slne.surf.skill.backend.experience

import com.google.auto.service.AutoService
import dev.slne.surf.skill.api.experience.SkillExperience
import dev.slne.surf.skill.backend.db.repository.ExperienceRepository
import dev.slne.surf.skill.core.experience.ExperienceService
import dev.slne.surf.surfapi.core.api.util.toObjectList
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(ExperienceService::class)
class ExperienceServiceImpl : ExperienceService, Services.Fallback {
    override suspend fun fetchPlayerExperience(uuid: UUID): ObjectList<SkillExperience> {
        return ExperienceRepository.findExperiences(uuid).map { it as SkillExperience }
            .toObjectList()
    }

    override suspend fun savePlayerExperience(
        uuid: UUID,
        experience: ObjectList<SkillExperience>
    ) {
        experience.forEach { experience ->
            ExperienceRepository.saveExperience(uuid, experience)
        }
    }
}