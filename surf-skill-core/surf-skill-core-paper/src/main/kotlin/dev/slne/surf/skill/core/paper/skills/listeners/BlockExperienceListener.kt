package dev.slne.surf.skill.core.paper.skills.listeners

import dev.slne.surf.skill.core.paper.util.BlockExperienceHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockFertilizeEvent
import org.bukkit.event.block.BlockGrowEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.world.StructureGrowEvent

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

    @EventHandler(priority = EventPriority.MONITOR)
    fun onStructureGrow(event: StructureGrowEvent) {
        if (event.isCancelled) return

        BlockExperienceHandler.handleStructureGrow(event)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockFertilize(event: BlockFertilizeEvent) {
        if (event.isCancelled) return

        BlockExperienceHandler.handleBlockFertilize(event)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockGrow(event: BlockGrowEvent) {
        if (event.isCancelled) return

        BlockExperienceHandler.handleBlockGrow(event)
    }
}