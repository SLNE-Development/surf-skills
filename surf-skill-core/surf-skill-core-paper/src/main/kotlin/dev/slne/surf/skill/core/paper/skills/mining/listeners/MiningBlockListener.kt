@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.paper.skills.mining.listeners

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.skill.api.paper.SkillInstance
import dev.slne.surf.skill.api.paper.player.incrementExperience
import dev.slne.surf.skill.api.paper.player.skillPlayer
import dev.slne.surf.skill.api.paper.skills.MiningSkill
import dev.slne.surf.skill.core.paper.util.SkillLevelingHandler
import dev.slne.surf.skill.core.paper.util.isEligibleForExperience
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent
import java.util.*
import kotlin.time.Duration.Companion.seconds

object MiningBlockListener : Listener {
    private val instantBreakCache = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterWrite(2.seconds)
        .build<BlockBreakKey, Boolean>()

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBlockDamage(event: BlockDamageEvent) {
        val block = event.block

        if (block.type != Material.DEEPSLATE) {
            return
        }

        instantBreakCache.put(
            BlockBreakKey.of(event.player.uniqueId, block),
            event.instaBreak
        )
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block

        val gotInstantBroken =
            block.type == Material.DEEPSLATE &&
                    handleInstantBreak(player.uniqueId, block)

        var exp = blockExpMap[block.type] ?: return

        if (!block.isEligibleForExperience()) {
            return
        }

        if (!SkillLevelingHandler.canCollectExperience(player, MiningSkill)) {
            return
        }

        if (gotInstantBroken) {
            exp /= 2
        }

        SkillInstance.launch {
            player.skillPlayer().incrementExperience<MiningSkill>(exp)
        }
    }

    private fun handleInstantBreak(playerUuid: UUID, block: Block): Boolean {
        val key = BlockBreakKey.of(playerUuid, block)
        val instantBreak = instantBreakCache.getIfPresent(key) == true

        instantBreakCache.invalidate(key)
        return instantBreak
    }

    private data class BlockBreakKey(
        val playerUuid: UUID,
        val worldUuid: UUID,
        val x: Int,
        val y: Int,
        val z: Int
    ) {
        companion object {
            fun of(playerUuid: UUID, block: Block) = BlockBreakKey(
                playerUuid = playerUuid,
                worldUuid = block.world.uid,
                x = block.x,
                y = block.y,
                z = block.z
            )
        }
    }

    private val blockExpMap = mapOf(
        // --- BASIC NETHER STONES ---
        Material.NETHERRACK to 3,
        Material.WARPED_NYLIUM to 3,
        Material.CRIMSON_NYLIUM to 3,

        // --- STONE BASE ---
        Material.STONE to 4,
        Material.COBBLESTONE to 4,
        Material.SANDSTONE to 4,

        // --- MID STONE VARIANTS ---
        Material.DIORITE to 5,
        Material.GRANITE to 5,
        Material.ANDESITE to 5,
        Material.STONE_BRICKS to 5,
        Material.MOSSY_COBBLESTONE to 5,
        Material.MOSSY_STONE_BRICKS to 5,
        Material.DRIPSTONE_BLOCK to 5,

        Material.TERRACOTTA to 5,
        Material.RED_TERRACOTTA to 5,
        Material.ORANGE_TERRACOTTA to 5,
        Material.YELLOW_TERRACOTTA to 5,
        Material.BROWN_TERRACOTTA to 5,
        Material.WHITE_TERRACOTTA to 5,
        Material.LIGHT_GRAY_TERRACOTTA to 5,
        Material.GRAY_TERRACOTTA to 5,
        Material.BLACK_TERRACOTTA to 5,
        Material.LIGHT_BLUE_TERRACOTTA to 5,
        Material.CYAN_TERRACOTTA to 5,
        Material.BLUE_TERRACOTTA to 5,
        Material.PURPLE_TERRACOTTA to 5,
        Material.MAGENTA_TERRACOTTA to 5,
        Material.PINK_TERRACOTTA to 5,
        Material.LIME_TERRACOTTA to 5,
        Material.GREEN_TERRACOTTA to 5,

        // --- DEEP STONE / NETHER HARD ---
        Material.TUFF to 6,
        Material.BLACKSTONE to 6,
        Material.DEEPSLATE to 6,
        Material.CALCITE to 6,

        Material.END_STONE to 6,
        Material.BASALT to 6,
        Material.SMOOTH_BASALT to 6,

        // --- RARE NETHER BLOCKS ---
        Material.GILDED_BLACKSTONE to 20,

        Material.SULFUR to 20,
        Material.POTENT_SULFUR to 20,
        Material.SULFUR_SPIKE to 30,

        // --- LOW ORES ---
        Material.COAL_ORE to 60,
        Material.COPPER_ORE to 60,
        Material.NETHER_QUARTZ_ORE to 60,
        Material.NETHER_GOLD_ORE to 75,
        Material.GLOWSTONE to 50,

        // --- MID ORES ---
        Material.IRON_ORE to 110,
        Material.DEEPSLATE_IRON_ORE to 140,
        Material.DEEPSLATE_COPPER_ORE to 160,
        Material.AMETHYST_BLOCK to 30,

        // --- SPECIAL MID ---
        Material.OBSIDIAN to 120,
        Material.CRYING_OBSIDIAN to 240,
        Material.RAW_COPPER_BLOCK to 250,
        Material.RAW_GOLD_BLOCK to 250,

        // --- HIGH ORES ---
        Material.LAPIS_ORE to 175,
        Material.DEEPSLATE_LAPIS_ORE to 210,
        Material.REDSTONE_ORE to 175,
        Material.DEEPSLATE_REDSTONE_ORE to 210,

        // --- VERY HIGH ---
        Material.GOLD_ORE to 280,
        Material.DEEPSLATE_GOLD_ORE to 330,
        Material.DIAMOND_ORE to 360,
        Material.DEEPSLATE_DIAMOND_ORE to 300,
        Material.BUDDING_AMETHYST to 75,
        Material.RAW_IRON_BLOCK to 250,

        // --- EXTREME ---
        Material.ANCIENT_DEBRIS to 750,

        // --- END RARE ---
        Material.EMERALD_ORE to 1000,
        Material.DEEPSLATE_COAL_ORE to 2000,
        Material.DEEPSLATE_EMERALD_ORE to 20000
    )
}
