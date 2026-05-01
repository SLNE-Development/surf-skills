package dev.slne.surf.skill.core.paper.skills.listeners

import dev.slne.surf.skill.core.paper.util.BlockExperienceHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

object BlockExperienceListener : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (event.isCancelled) return

        BlockExperienceHandler.handleBlockPlace(event)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onBlockBreak(event: BlockBreakEvent) {
        if (event.isCancelled) return

        BlockExperienceHandler.handleBlockBreak(event)
    }
}