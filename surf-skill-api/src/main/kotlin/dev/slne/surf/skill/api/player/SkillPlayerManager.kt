package dev.slne.surf.skill.api.player

import dev.slne.surf.api.core.util.requiredService
import java.util.*

private val playerManager = requiredService<SkillPlayerManager>()

interface SkillPlayerManager {
    suspend fun fetchOrCreatePlayer(uuid: UUID): SkillPlayer

    fun getPlayerIfCached(uuid: UUID): SkillPlayer?

    suspend fun savePlayer(uuid: UUID)
    suspend fun savePlayer(player: SkillPlayer)

    fun invalidatePlayer(uuid: UUID)

    companion object : SkillPlayerManager by playerManager {
        val INSTANCE get() = playerManager
    }
}