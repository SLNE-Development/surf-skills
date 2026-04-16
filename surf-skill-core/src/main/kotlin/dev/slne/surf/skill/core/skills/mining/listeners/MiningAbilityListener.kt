@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.skills.mining.listeners

import dev.slne.surf.skill.api.skills.MiningSkill
import dev.slne.surf.skill.core.ability.AbilityUtil
import dev.slne.surf.skill.core.skills.utils.isEligibleForExperience
import org.bukkit.Material
import org.bukkit.block.BlockType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object MiningAbilityListener : Listener {

    // ── Skillful Extraction ──
    // Pickaxes lose 0% → 50% less durability
    // Available from level 1
    private const val SKILLFUL_EXTRACTION_MIN_LEVEL = 1
    private const val SKILLFUL_EXTRACTION_MAX_VALUE = 0.50

    private val PICKAXE_TYPES = setOf(
        Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE,
        Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE
    )

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onSkillfulExtraction(event: PlayerItemDamageEvent) {
        val player = event.player
        val itemType = event.item.type

        if (itemType !in PICKAXE_TYPES) return

        val level = AbilityUtil.getPlayerLevel<MiningSkill>(player)
        val reductionChance = AbilityUtil.calculateScaledValue(
            level, SKILLFUL_EXTRACTION_MIN_LEVEL, maxValue = SKILLFUL_EXTRACTION_MAX_VALUE
        )

        if (AbilityUtil.rollChance(reductionChance)) {
            event.damage = 0
        }
    }

    // ── Spelunking ──
    // 0% → 20% chance to receive 2x drops from ores
    // Available from level 11
    private const val SPELUNKING_MIN_LEVEL = 11
    private const val SPELUNKING_MAX_VALUE = 0.20

    private val ORE_BLOCKS = setOf(
        BlockType.COAL_ORE, BlockType.DEEPSLATE_COAL_ORE,
        BlockType.IRON_ORE, BlockType.DEEPSLATE_IRON_ORE,
        BlockType.COPPER_ORE, BlockType.DEEPSLATE_COPPER_ORE,
        BlockType.GOLD_ORE, BlockType.DEEPSLATE_GOLD_ORE,
        BlockType.REDSTONE_ORE, BlockType.DEEPSLATE_REDSTONE_ORE,
        BlockType.EMERALD_ORE, BlockType.DEEPSLATE_EMERALD_ORE,
        BlockType.LAPIS_ORE, BlockType.DEEPSLATE_LAPIS_ORE,
        BlockType.DIAMOND_ORE, BlockType.DEEPSLATE_DIAMOND_ORE,
        BlockType.NETHER_GOLD_ORE, BlockType.NETHER_QUARTZ_ORE,
        BlockType.ANCIENT_DEBRIS
    )

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onSpelunking(event: BlockDropItemEvent) {
        val player = event.player
        val blockType = event.blockState.type.asBlockType() ?: return

        if (blockType !in ORE_BLOCKS) return
        if (!event.block.isEligibleForExperience()) return

        val level = AbilityUtil.getPlayerLevel<MiningSkill>(player)
        val chance = AbilityUtil.calculateScaledValue(
            level, SPELUNKING_MIN_LEVEL, maxValue = SPELUNKING_MAX_VALUE
        )

        if (AbilityUtil.rollChance(chance)) {
            event.items.forEach { item ->
                val extra = item.itemStack.clone()
                player.world.dropItemNaturally(item.location, extra)
            }
        }
    }

    // ── Dynamic Mining ──
    // 0% → 1% chance when mining stone or deepslate to receive Haste VIII for 3 → 12 seconds
    // Available from level 21
    private const val DYNAMIC_MINING_MIN_LEVEL = 21
    private const val DYNAMIC_MINING_MAX_CHANCE = 0.01
    private const val DYNAMIC_MINING_MIN_DURATION_TICKS = 3 * 20
    private const val DYNAMIC_MINING_MAX_DURATION_TICKS = 12 * 20
    private const val DYNAMIC_MINING_HASTE_AMPLIFIER = 7 // Haste VIII (0-indexed)

    private val STONE_TYPES = setOf(
        Material.STONE, Material.DEEPSLATE,
        Material.COBBLESTONE, Material.COBBLED_DEEPSLATE,
        Material.ANDESITE, Material.DIORITE, Material.GRANITE,
        Material.TUFF, Material.CALCITE, Material.DRIPSTONE_BLOCK
    )

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onDynamicMining(event: BlockBreakEvent) {
        val player = event.player
        val blockType = event.block.type

        if (blockType !in STONE_TYPES) return

        val level = AbilityUtil.getPlayerLevel<MiningSkill>(player)
        val chance = AbilityUtil.calculateScaledValue(
            level, DYNAMIC_MINING_MIN_LEVEL, maxValue = DYNAMIC_MINING_MAX_CHANCE
        )

        if (AbilityUtil.rollChance(chance)) {
            val durationTicks = AbilityUtil.calculateScaledValue(
                level, DYNAMIC_MINING_MIN_LEVEL,
                maxValue = DYNAMIC_MINING_MAX_DURATION_TICKS.toDouble()
            ).toInt().coerceAtLeast(DYNAMIC_MINING_MIN_DURATION_TICKS)

            player.addPotionEffect(
                PotionEffect(
                    PotionEffectType.HASTE,
                    durationTicks,
                    DYNAMIC_MINING_HASTE_AMPLIFIER,
                    true,
                    true,
                    true
                )
            )
        }
    }
}
