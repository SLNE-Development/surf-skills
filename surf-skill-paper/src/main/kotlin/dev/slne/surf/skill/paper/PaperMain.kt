package dev.slne.surf.skill.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.api.paper.inventory.framework.register
import dev.slne.surf.skill.api.paper.level.reward.rewards.RewardGrantLogger
import dev.slne.surf.skill.api.paper.player.SkillPlayerManager
import dev.slne.surf.skill.core.paper.PaperSkillInstance
import dev.slne.surf.skill.core.paper.manager.skillManagerImpl
import dev.slne.surf.skill.core.paper.settings.SettingsHook
import dev.slne.surf.skill.core.paper.settings.hasSettingsApi
import dev.slne.surf.skill.paper.commands.skillCommand
import dev.slne.surf.skill.paper.listener.ListenerManager
import dev.slne.surf.skill.paper.listener.StatsDiffSaveListener
import dev.slne.surf.skill.paper.menu.settings.skillSettingsView
import dev.slne.surf.skill.paper.menu.skillView
import dev.slne.surf.skill.paper.menu.skillsView
import org.bukkit.plugin.java.JavaPlugin

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

        StatsDiffSaveListener.start()

        skillCommand()
    }

    override suspend fun onDisableAsync() {
        StatsDiffSaveListener.stop()

        server.onlinePlayers.forEach { player ->
            val uuid = player.uniqueId

            SkillPlayerManager.savePlayer(uuid)
            SkillPlayerManager.invalidatePlayer(uuid)
        }

        PaperSkillInstance.paperLoader.onDisable()
    }
}

val plugin = JavaPlugin.getPlugin(PaperMain::class.java)