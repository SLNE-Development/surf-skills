package dev.slne.surf.skill.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.skill.core.manager.skillManagerImpl
import dev.slne.surf.skill.paper.commands.skillCommand
import dev.slne.surf.skill.paper.listener.ListenerManager
import dev.slne.surf.skill.paper.menu.SkillView
import dev.slne.surf.skill.paper.menu.SkillsView
import dev.slne.surf.surfapi.bukkit.api.inventory.framework.viewFrame
import org.bukkit.plugin.java.JavaPlugin

class PaperMain : SuspendingJavaPlugin() {
    override suspend fun onLoadAsync() {
        viewFrame.with(SkillsView())
        viewFrame.with(SkillView())
    }

    override suspend fun onEnableAsync() {
        skillCommand()

        skillManagerImpl.registerAllSkills()
        ListenerManager.register()
    }

    override suspend fun onDisableAsync() {
        ListenerManager.unregister()
    }
}

val plugin = JavaPlugin.getPlugin(PaperMain::class.java)