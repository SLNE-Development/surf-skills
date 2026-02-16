package dev.slne.surf.skill.paper.listener

import dev.slne.surf.skill.core.manager.skillManagerImpl
import dev.slne.surf.surfapi.bukkit.api.event.register

object ListenerManager {
    fun register() {
        skillManagerImpl.registerListeners()
        ExperienceServiceListener.register()
    }
}