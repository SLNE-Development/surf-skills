@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.paper.skills.woodcutting.listeners

import dev.slne.surf.skill.api.paper.skills.WoodcuttingSkill
import dev.slne.surf.skill.core.paper.ability.AbilityUtil
import dev.slne.surf.skill.core.paper.util.isEligibleForExperience
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.BlockType
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.player.PlayerItemDamageEvent

object WoodcuttingAbilityListener : Listener {
    private const val CRAFTSMANSHIP_MIN_LEVEL = 1
    private const val CRAFTSMANSHIP_MAX_VALUE = 0.50

    private val AXE_TYPES = setOf(
        Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE,
        Material.COPPER_AXE,
        Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE
    )

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onCraftsmanship(event: PlayerItemDamageEvent) {
        val player = event.player
        val itemType = event.item.type

        if (itemType !in AXE_TYPES) return

        val level = AbilityUtil.getPlayerLevel<WoodcuttingSkill>(player)
        val reductionChance = AbilityUtil.calculateScaledValue(
            level, CRAFTSMANSHIP_MIN_LEVEL, maxValue = CRAFTSMANSHIP_MAX_VALUE
        )

        if (AbilityUtil.rollChance(reductionChance)) {
            event.damage = 0
        }
    }

    private const val FORESTS_GIFT_MIN_LEVEL = 11
    private const val FORESTS_GIFT_MAX_VALUE = 0.20

    private val LOG_BLOCKS = setOf(
        BlockType.OAK_LOG, BlockType.SPRUCE_LOG, BlockType.BIRCH_LOG,
        BlockType.JUNGLE_LOG, BlockType.ACACIA_LOG, BlockType.DARK_OAK_LOG,
        BlockType.MANGROVE_LOG, BlockType.CHERRY_LOG, BlockType.PALE_OAK_LOG,
        BlockType.CRIMSON_STEM, BlockType.WARPED_STEM,
        BlockType.STRIPPED_OAK_LOG, BlockType.STRIPPED_SPRUCE_LOG,
        BlockType.STRIPPED_BIRCH_LOG, BlockType.STRIPPED_JUNGLE_LOG,
        BlockType.STRIPPED_ACACIA_LOG, BlockType.STRIPPED_DARK_OAK_LOG,
        BlockType.STRIPPED_MANGROVE_LOG, BlockType.STRIPPED_CHERRY_LOG,
        BlockType.STRIPPED_PALE_OAK_LOG,
        BlockType.STRIPPED_CRIMSON_STEM, BlockType.STRIPPED_WARPED_STEM,
        BlockType.OAK_WOOD, BlockType.SPRUCE_WOOD, BlockType.BIRCH_WOOD,
        BlockType.JUNGLE_WOOD, BlockType.ACACIA_WOOD, BlockType.DARK_OAK_WOOD,
        BlockType.MANGROVE_WOOD, BlockType.CHERRY_WOOD, BlockType.PALE_OAK_WOOD,
        BlockType.CRIMSON_HYPHAE, BlockType.WARPED_HYPHAE,
        BlockType.STRIPPED_OAK_WOOD, BlockType.STRIPPED_SPRUCE_WOOD,
        BlockType.STRIPPED_BIRCH_WOOD, BlockType.STRIPPED_JUNGLE_WOOD,
        BlockType.STRIPPED_ACACIA_WOOD, BlockType.STRIPPED_DARK_OAK_WOOD,
        BlockType.STRIPPED_MANGROVE_WOOD, BlockType.STRIPPED_CHERRY_WOOD,
        BlockType.STRIPPED_PALE_OAK_WOOD,
        BlockType.STRIPPED_CRIMSON_HYPHAE, BlockType.STRIPPED_WARPED_HYPHAE
    )

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onForestsGift(event: BlockDropItemEvent) {
        val player = event.player
        val blockType = event.blockState.type.asBlockType() ?: return

        if (blockType !in LOG_BLOCKS) return
        if (!event.block.isEligibleForExperience()) return

        val level = AbilityUtil.getPlayerLevel<WoodcuttingSkill>(player)
        val chance = AbilityUtil.calculateScaledValue(
            level, FORESTS_GIFT_MIN_LEVEL, maxValue = FORESTS_GIFT_MAX_VALUE
        )

        if (AbilityUtil.rollChance(chance)) {
            event.items.forEach {
                it.world.dropItemNaturally(it.location, it.itemStack.clone())
            }

            player.spawnParticle(Particle.TRIAL_SPAWNER_DETECTION, event.block.location, 5)
        }
    }

    private const val MASTER_LUMBERJACK_MIN_LEVEL = 21
    private const val MASTER_LUMBERJACK_MAX_VALUE = 15.0

    fun getLumberjackCooldownReduction(level: Int): Double = AbilityUtil.calculateScaledValue(
        level, MASTER_LUMBERJACK_MIN_LEVEL, maxValue = MASTER_LUMBERJACK_MAX_VALUE
    )
}
