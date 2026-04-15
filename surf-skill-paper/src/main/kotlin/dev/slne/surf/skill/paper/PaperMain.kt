package dev.slne.surf.skill.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.api.paper.inventory.framework.register
import dev.slne.surf.skill.api.player.SkillPlayerManager
import dev.slne.surf.skill.core.manager.skillManagerImpl
import dev.slne.surf.skill.paper.commands.skillCommand
import dev.slne.surf.skill.paper.listener.ListenerManager
import dev.slne.surf.skill.paper.menu.skillView
import dev.slne.surf.skill.paper.menu.skillsView
import org.bukkit.plugin.java.JavaPlugin

class PaperMain : SuspendingJavaPlugin() {
    override suspend fun onLoadAsync() {

        skillsView.register()
        skillView.register()
    }

    override suspend fun onEnableAsync() {
        skillCommand()

        skillManagerImpl.registerAllSkills()
        ListenerManager.register()
    }

    override suspend fun onDisableAsync() {
        server.onlinePlayers.forEach { player ->
            val uuid = player.uniqueId

            SkillPlayerManager.savePlayer(uuid)
            SkillPlayerManager.invalidatePlayer(uuid)
        }
    }
}

val plugin = JavaPlugin.getPlugin(PaperMain::class.java)