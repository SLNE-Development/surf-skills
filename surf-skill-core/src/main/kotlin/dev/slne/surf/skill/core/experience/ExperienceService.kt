package dev.slne.surf.skill.core.experience

import dev.slne.surf.skill.api.experience.SkillExperience
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectList
import java.util.*

private val experienceService = requiredService<ExperienceService>()

interface ExperienceService {
    suspend fun getExperiencesForPlayer(uuid: UUID): ObjectList<SkillExperience>

    suspend fun savePlayer(uuid: UUID)
    suspend fun invalidatePlayer(uuid: UUID)

    companion object : ExperienceService by experienceService {
        val INSTANCE get() = experienceService
    }
}