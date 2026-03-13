package dev.slne.surf.skill.core.skills.listeners

import dev.slne.surf.skill.core.skills.utils.BlockExperienceHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

object BlockExperienceListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (event.isCancelled) return

        BlockExperienceHandler.handleBlockPlace(event)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockBreak(event: BlockBreakEvent) {
        if (event.isCancelled) return

        BlockExperienceHandler.handleBlockBreak(event)
    }
}