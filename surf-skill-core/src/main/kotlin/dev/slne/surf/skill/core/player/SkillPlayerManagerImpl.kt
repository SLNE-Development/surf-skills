package dev.slne.surf.skill.core.player

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.auto.service.AutoService
import com.sksamuel.aedile.core.asLoadingCache
import dev.slne.surf.skill.api.player.SkillPlayer
import dev.slne.surf.skill.api.player.SkillPlayerManager
import dev.slne.surf.skill.core.experience.ExperienceService
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(SkillPlayerManager::class)
class SkillPlayerManagerImpl : SkillPlayerManager, Services.Fallback {
    private val cache = Caffeine.newBuilder()
        .maximumSize(10_000)
        .asLoadingCache<UUID, SkillPlayer> { uuid ->
            val experiences = ExperienceService.fetchPlayerExperience(uuid)

            SkillPlayerImpl(uuid, experiences)
        }

    override suspend fun fetchOrCreatePlayer(uuid: UUID): SkillPlayer =
        cache.get(uuid)

    override suspend fun savePlayer(uuid: UUID) {
        val player = cache.getIfPresent(uuid) ?: return

        savePlayer(player)
    }

    override suspend fun savePlayer(player: SkillPlayer) {
        ExperienceService.savePlayerExperience(player.uuid, player.experiences)
    }

    override fun invalidatePlayer(uuid: UUID) {
        cache.invalidate(uuid)
    }
}