package dev.slne.surf.skill.core.paper.skills.mining.listeners

import dev.slne.surf.skill.api.SkillInstance
import dev.slne.surf.skill.api.player.incrementExperience
import dev.slne.surf.skill.api.player.skillPlayer
import dev.slne.surf.skill.api.skills.MiningSkill
import dev.slne.surf.skill.core.paper.skills.utils.isEligibleForExperience
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

object MiningBlockListener : Listener {
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player

        if (event.isCancelled) {
            return
        }

        val exp = blockExpMap[event.block.type] ?: return

        if (!event.block.isEligibleForExperience()) {
            return
        }

        SkillInstance.launch {
            player.skillPlayer().incrementExperience<MiningSkill>(exp)
        }
    }


    private val blockExpMap = mapOf(

        // --- OVERWORLD BASIC (0–2) ---
        Material.DIRT to 0,
        Material.GRASS_BLOCK to 1,
        Material.COARSE_DIRT to 0,
        Material.ROOTED_DIRT to 1,
        Material.PODZOL to 1,
        Material.MYCELIUM to 1,
        Material.SAND to 0,
        Material.RED_SAND to 0,
        Material.GRAVEL to 1,
        Material.CLAY to 2,
        Material.MUD to 1,
        Material.PACKED_MUD to 1,

        // --- STONE TYPES (1–3) ---
        Material.STONE to 1,
        Material.COBBLESTONE to 0,
        Material.DEEPSLATE to 2,
        Material.COBBLED_DEEPSLATE to 0,
        Material.TUFF to 2,
        Material.CALCITE to 2,
        Material.ANDESITE to 1,
        Material.DIORITE to 1,
        Material.GRANITE to 1,

        // --- CAVE / NATURE (2–4) ---
        Material.DRIPSTONE_BLOCK to 3,
        Material.POINTED_DRIPSTONE to 2,
        Material.MOSS_BLOCK to 2,
        Material.MOSS_CARPET to 1,
        Material.GLOW_LICHEN to 1,
        Material.SPORE_BLOSSOM to 3,
        Material.AZALEA to 2,
        Material.FLOWERING_AZALEA to 2,

        // --- OVERWORLD ORES (5–10) ---
        Material.COAL_ORE to 5,
        Material.DEEPSLATE_COAL_ORE to 5,
        Material.COPPER_ORE to 5,
        Material.DEEPSLATE_COPPER_ORE to 5,
        Material.IRON_ORE to 6,
        Material.DEEPSLATE_IRON_ORE to 6,
        Material.GOLD_ORE to 6,
        Material.DEEPSLATE_GOLD_ORE to 6,
        Material.REDSTONE_ORE to 6,
        Material.DEEPSLATE_REDSTONE_ORE to 6,
        Material.LAPIS_ORE to 7,
        Material.DEEPSLATE_LAPIS_ORE to 7,
        Material.DIAMOND_ORE to 9,
        Material.DEEPSLATE_DIAMOND_ORE to 9,
        Material.EMERALD_ORE to 10,
        Material.DEEPSLATE_EMERALD_ORE to 10,

        // --- AMETHYST (7–9) ---
        Material.AMETHYST_BLOCK to 7,
        Material.BUDDING_AMETHYST to 9,
        Material.SMALL_AMETHYST_BUD to 7,
        Material.MEDIUM_AMETHYST_BUD to 8,
        Material.LARGE_AMETHYST_BUD to 8,
        Material.AMETHYST_CLUSTER to 9,

        // --- NETHER BASIC (1–3) ---
        Material.NETHERRACK to 1,
        Material.SOUL_SAND to 2,
        Material.SOUL_SOIL to 2,
        Material.BASALT to 2,
        Material.SMOOTH_BASALT to 2,
        Material.BLACKSTONE to 3,
        Material.GILDED_BLACKSTONE to 5,

        // --- NETHER VEGETATION / LIGHT (2–4) ---
        Material.GLOWSTONE to 4,
        Material.SHROOMLIGHT to 3,
        Material.NETHER_WART_BLOCK to 2,
        Material.WARPED_WART_BLOCK to 2,
        Material.CRIMSON_STEM to 2,
        Material.WARPED_STEM to 2,

        // --- NETHER ORES (5–10) ---
        Material.NETHER_QUARTZ_ORE to 5,
        Material.NETHER_GOLD_ORE to 6,
        Material.ANCIENT_DEBRIS to 10,

        // --- END BASIC (2–4) ---
        Material.END_STONE to 2,
        Material.END_STONE_BRICKS to 1,

        // --- END VEGETATION (3–5) ---
        Material.CHORUS_PLANT to 3,
        Material.CHORUS_FLOWER to 4,

        // --- END STRUCTURES (3–5) ---
        Material.PURPUR_BLOCK to 4,
        Material.PURPUR_PILLAR to 4,
        Material.PURPUR_STAIRS to 3,
        Material.PURPUR_SLAB to 3,
        Material.END_ROD to 5,

        // --- END RARE (7–10) ---
        Material.OBSIDIAN to 7,
        Material.CRYING_OBSIDIAN to 8
    )
}