package dev.slne.surf.skill.paper.listener

import dev.slne.skill.core.manager.skillManagerImpl

object ListenerManager {
    fun register() {
        skillManagerImpl.registerListeners()
    }

    fun unregister() {
        skillManagerImpl.unregisterListeners()
    }
}