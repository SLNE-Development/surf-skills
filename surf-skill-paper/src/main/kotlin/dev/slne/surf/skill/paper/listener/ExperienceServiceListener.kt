package dev.slne.surf.skill.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.skill.api.player.SkillPlayerManager
import dev.slne.surf.skill.paper.plugin
import dev.slne.surf.surfapi.bukkit.api.extensions.server
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
            SkillPlayerManager.fetchOrCreatePlayer(uuid)
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        val uuid = player.uniqueId

        plugin.launch {
            SkillPlayerManager.savePlayer(uuid)
            SkillPlayerManager.invalidatePlayer(uuid)
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