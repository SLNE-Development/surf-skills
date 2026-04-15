package dev.slne.surf.skill.core.experience

import dev.slne.surf.skill.api.experience.SkillExperience
import dev.slne.surf.api.core.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectList
import java.util.*

private val experienceService = requiredService<ExperienceService>()

interface ExperienceService {
    suspend fun fetchPlayerExperience(uuid: UUID): ObjectList<SkillExperience>
    suspend fun savePlayerExperience(uuid: UUID, experience: ObjectList<SkillExperience>)

    companion object : ExperienceService by experienceService {
        val INSTANCE get() = experienceService
    }
}