package dev.slne.surf.skill.paper.listener

import dev.slne.surf.api.paper.event.register
import dev.slne.surf.skill.core.paper.manager.skillManagerImpl
import dev.slne.surf.skill.core.paper.skills.listeners.BlockExperienceListener

object ListenerManager {
    fun register() {
        skillManagerImpl.registerListeners()
        ExperienceServiceListener.register()
        BlockExperienceListener.register()
    }
}