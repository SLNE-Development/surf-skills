package dev.slne.surf.skill.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.scope
import dev.slne.surf.api.core.util.runAtFixedRate
import dev.slne.surf.api.paper.inventory.framework.register
import dev.slne.surf.skill.api.paper.level.reward.rewards.RewardGrantLogger
import dev.slne.surf.skill.api.paper.player.SkillPlayerManager
import dev.slne.surf.skill.core.paper.PaperSkillInstance
import dev.slne.surf.skill.core.paper.manager.skillManagerImpl
import dev.slne.surf.skill.core.paper.settings.SettingsHook
import dev.slne.surf.skill.core.paper.settings.hasSettingsApi
import dev.slne.surf.skill.core.paper.stats.hasStatsApi
import dev.slne.surf.skill.paper.commands.skillCommand
import dev.slne.surf.skill.paper.listener.ListenerManager
import dev.slne.surf.skill.paper.listener.StatsDiffSaveListener
import dev.slne.surf.skill.paper.menu.settings.skillSettingsView
import dev.slne.surf.skill.paper.menu.skillView
import dev.slne.surf.skill.paper.menu.skillsView
import kotlinx.coroutines.withTimeoutOrNull
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class PaperMain : SuspendingJavaPlugin() {
    override suspend fun onLoadAsync() {
        PaperSkillInstance.paperLoader.onLoad()

        skillsView.register()
        skillView.register()
        skillSettingsView.register()
    }

    override suspend fun onEnableAsync() {
        skillManagerImpl.registerAllSkills()
        ListenerManager.register()
        RewardGrantLogger.createAuxProtectHookIfAvailable()

        if (hasSettingsApi()) {
            SettingsHook.registerSettings()
        }

        if (hasStatsApi) {
            StatsDiffSaveListener.start()
        }

        plugin.scope.runAtFixedRate(5.minutes, 5.minutes) {
            val saved = ConcurrentHashMap.newKeySet<UUID>()

            Bukkit.getOnlinePlayers().forEach { player ->
                val uuid = player.uniqueId

                launch {
                    SkillPlayerManager.savePlayer(uuid)
                    saved.add(uuid)
                }
            }

            if (saved.isNotEmpty()) {
                logger.info("Saved ${saved.size} skill players!")
            }
        }

        skillCommand()
    }

    override suspend fun onDisableAsync() {
        if (hasStatsApi) {
            withTimeoutOrNull(10.seconds) {
                StatsDiffSaveListener.stop()
            }
                ?: logger.severe("Failed to save all skill player diffs before shutdown! Aborted after 10 seconds.")
        }
        
        server.onlinePlayers.forEach { player ->
            val uuid = player.uniqueId

            SkillPlayerManager.savePlayer(uuid)
            SkillPlayerManager.invalidatePlayer(uuid)
        }

        PaperSkillInstance.paperLoader.onDisable()
    }
}

val plugin = JavaPlugin.getPlugin(PaperMain::class.java)