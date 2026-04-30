@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.paper.skills.woodcutting.listeners

import dev.slne.surf.skill.api.paper.skills.WoodcuttingSkill
import dev.slne.surf.skill.core.paper.ability.AbilityUtil
import dev.slne.surf.skill.core.paper.skills.utils.isEligibleForExperience
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.BlockType
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.player.PlayerItemDamageEvent

object WoodcuttingAbilityListener : Listener {
    private const val CRAFTSMANSHIP_MIN_LEVEL = 1
    private const val CRAFTSMANSHIP_MAX_VALUE = 0.50

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onCraftsmanship(event: PlayerItemDamageEvent) {
        val player = event.player
        val itemType = event.item.type

        if (!Tag.ITEMS_AXES.isTagged(itemType)) return

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

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onForestsGift(event: BlockDropItemEvent) {
        val player = event.player
        val blockType = event.blockState.type

        if (!Tag.LOGS.isTagged(blockType)) return
        if (!event.block.isEligibleForExperience()) return

        val level = AbilityUtil.getPlayerLevel<WoodcuttingSkill>(player)
        val chance = AbilityUtil.calculateScaledValue(
            level, FORESTS_GIFT_MIN_LEVEL, maxValue = FORESTS_GIFT_MAX_VALUE
        )

        if (AbilityUtil.rollChance(chance)) {
            event.items.forEach { item ->
                val extra = item.itemStack.clone()
                player.world.dropItemNaturally(item.location, extra)
            }
        }
    }

    private const val MASTER_LUMBERJACK_MIN_LEVEL = 21
    private const val MASTER_LUMBERJACK_MAX_VALUE = 15.0

    fun getLumberjackCooldownReduction(level: Int): Double = AbilityUtil.calculateScaledValue(
        level, MASTER_LUMBERJACK_MIN_LEVEL, maxValue = MASTER_LUMBERJACK_MAX_VALUE
    )
}
