package dev.slne.surf.skill.core.paper.skills.woodcutting.listeners

import dev.slne.surf.skill.api.paper.SkillInstance
import dev.slne.surf.skill.api.paper.player.incrementExperience
import dev.slne.surf.skill.api.paper.player.skillPlayer
import dev.slne.surf.skill.api.paper.skills.WoodcuttingSkill
import dev.slne.surf.skill.core.paper.util.SkillLevelingHandler
import dev.slne.surf.skill.core.paper.util.isEligibleForExperience
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

object WoodcuttingSkillListener : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player

        if (event.isCancelled) {
            return
        }

        val exp = logExpMap[event.block.type] ?: return

        if (!event.block.isEligibleForExperience()) {
            return
        }

        if (!SkillLevelingHandler.canCollectExperience(event.player, WoodcuttingSkill)) {
            return
        }

        SkillInstance.launch {
            player.skillPlayer().incrementExperience<WoodcuttingSkill>(exp)
        }
    }


    private val logExpMap = mapOf(

        // --- BASIC WOODS ---
        Material.OAK_LOG to 40,
        Material.BIRCH_LOG to 40,
        Material.SPRUCE_LOG to 40,
        Material.JUNGLE_LOG to 40,
        Material.ACACIA_LOG to 40,
        Material.DARK_OAK_LOG to 40,
        Material.PALE_OAK_LOG to 40,

        Material.OAK_WOOD to 40,
        Material.BIRCH_WOOD to 40,
        Material.SPRUCE_WOOD to 40,
        Material.JUNGLE_WOOD to 40,
        Material.ACACIA_WOOD to 40,
        Material.DARK_OAK_WOOD to 40,
        Material.PALE_OAK_WOOD to 40,

        // --- SPECIAL WOODS ---
        Material.MANGROVE_LOG to 55,
        Material.CHERRY_LOG to 55,
        Material.MANGROVE_WOOD to 55,
        Material.CHERRY_WOOD to 55,

        // --- NETHER WOODS ---
        Material.CRIMSON_STEM to 70,
        Material.WARPED_STEM to 70,

        Material.CRIMSON_HYPHAE to 70,
        Material.WARPED_HYPHAE to 70
    )
}