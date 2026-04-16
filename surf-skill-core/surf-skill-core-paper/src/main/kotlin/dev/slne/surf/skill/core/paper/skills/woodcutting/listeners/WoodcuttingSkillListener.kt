package dev.slne.surf.skill.core.paper.skills.woodcutting.listeners

import dev.slne.surf.skill.api.paper.SkillInstance
import dev.slne.surf.skill.api.paper.Skills.WoodcuttingSkill
import dev.slne.surf.skill.api.player.incrementExperience
import dev.slne.surf.skill.api.player.skillPlayer
import dev.slne.surf.skill.core.paper.skills.utils.isEligibleForExperience
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

object WoodcuttingSkillListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player

        if (event.isCancelled) {
            return
        }

        val exp = logExpMap[event.block.type] ?: return

        if (!event.block.isEligibleForExperience()) {
            return
        }

        SkillInstance.launch {
            player.skillPlayer().incrementExperience<WoodcuttingSkill>(exp)
        }
    }


    private val logExpMap = mapOf(
        Material.OAK_LOG to 1,
        Material.SPRUCE_LOG to 2,
        Material.BIRCH_LOG to 2,
        Material.JUNGLE_LOG to 3,
        Material.ACACIA_LOG to 3,
        Material.DARK_OAK_LOG to 4,
        Material.MANGROVE_LOG to 10,
        Material.CHERRY_LOG to 7,
        Material.PALE_OAK_LOG to 10,
        Material.CRIMSON_HYPHAE to 5,
        Material.WARPED_HYPHAE to 5
    )
}