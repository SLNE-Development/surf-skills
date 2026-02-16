package dev.slne.surf.skill.backend.experience

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.auto.service.AutoService
import com.sksamuel.aedile.core.asLoadingCache
import dev.slne.surf.skill.api.experience.SkillExperience
import dev.slne.surf.skill.backend.db.repository.ExperienceRepository
import dev.slne.surf.skill.core.experience.ExperienceService
import dev.slne.surf.skill.core.experience.SkillExperienceImpl
import dev.slne.surf.surfapi.core.api.util.toMutableObjectList
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(ExperienceService::class)
class ExperienceServiceImpl : ExperienceService, Services.Fallback {
    private val cache = Caffeine.newBuilder()
        .maximumSize(10_000)
        .asLoadingCache<UUID, ObjectList<SkillExperienceImpl>> { uuid ->
            ExperienceRepository.findExperiences(uuid)
        }

    override suspend fun getExperiencesForPlayer(uuid: UUID): ObjectList<SkillExperience> {
        return cache.get(uuid).map { it as SkillExperience }.toMutableObjectList()
    }

    override suspend fun savePlayer(uuid: UUID) {
        val experiences = getExperiencesForPlayer(uuid)

        experiences.forEach { ExperienceRepository.saveExperience(uuid, it as SkillExperienceImpl) }
    }

    override suspend fun invalidatePlayer(uuid: UUID) {
        cache.invalidate(uuid)
    }
}