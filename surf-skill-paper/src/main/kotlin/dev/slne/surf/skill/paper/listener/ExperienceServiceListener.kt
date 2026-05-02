package dev.slne.surf.skill.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.api.paper.extensions.server
import dev.slne.surf.skill.api.paper.player.SkillPlayerManager
import dev.slne.surf.skill.core.paper.stats.SkillStatsHook
import dev.slne.surf.skill.core.paper.stats.hasStatsApi
import dev.slne.surf.skill.paper.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.WorldSaveEvent

object ExperienceServiceListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        val uuid = player.uniqueId

        plugin.launch {
            val skillPlayer = SkillPlayerManager.fetchOrCreatePlayer(uuid)

            if (hasStatsApi()) {
                SkillStatsHook.seedSnapshot(skillPlayer)
            }
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        val uuid = player.uniqueId

        if (hasStatsApi()) {
            plugin.launch {
                val cached = SkillPlayerManager.getPlayerIfCached(uuid)
                if (cached != null) {
                    SkillStatsHook.flushDiff(cached)
                }
                SkillPlayerManager.savePlayer(uuid)
                SkillPlayerManager.invalidatePlayer(uuid)
                SkillStatsHook.dropSnapshot(uuid)
            }
        }
    }

    @EventHandler
    fun onWorldSave(event: WorldSaveEvent) {
        if (event.world != server.worlds.first()) return

        server.onlinePlayers.forEach { player ->
            val uuid = player.uniqueId

            plugin.launch {
                SkillPlayerManager.savePlayer(uuid)
            }
        }
    }
}