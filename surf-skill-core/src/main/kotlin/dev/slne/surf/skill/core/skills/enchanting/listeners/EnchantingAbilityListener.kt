package dev.slne.surf.skill.core.skills.enchanting.listeners

import dev.slne.surf.skill.api.skills.EnchantingSkill
import dev.slne.surf.skill.core.ability.AbilityUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.player.PlayerExpChangeEvent
import org.bukkit.event.player.PlayerItemDamageEvent

object EnchantingAbilityListener : Listener {
    private const val INSIGHT_MIN_LEVEL = 1
    private const val INSIGHT_MAX_VALUE = 0.50

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onEnchantersInsight(event: PlayerExpChangeEvent) {
        val player = event.player
        val originalXp = event.amount

        if (originalXp <= 0) return

        val level = AbilityUtil.getPlayerLevel<EnchantingSkill>(player)
        val bonus = AbilityUtil.calculateScaledValue(
            level, INSIGHT_MIN_LEVEL, maxValue = INSIGHT_MAX_VALUE
        )

        if (bonus > 0.0) {
            event.amount = (originalXp * (1.0 + bonus)).toInt().coerceAtLeast(originalXp)
        }
    }

    private const val FORTIFICATION_MIN_LEVEL = 11
    private const val FORTIFICATION_MAX_VALUE = 0.20

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onArcaneFortification(event: PlayerItemDamageEvent) {
        val player = event.player
        val item = event.item

        // Only apply to items that have enchantments
        if (item.enchantments.isEmpty()) return

        val level = AbilityUtil.getPlayerLevel<EnchantingSkill>(player)
        val chance = AbilityUtil.calculateScaledValue(
            level, FORTIFICATION_MIN_LEVEL, maxValue = FORTIFICATION_MAX_VALUE
        )

        if (AbilityUtil.rollChance(chance)) {
            event.damage = 0
        }
    }

    private const val MANA_POOL_MIN_LEVEL = 21
    private const val MANA_POOL_MAX_VALUE = 0.25

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onManaPool(event: EnchantItemEvent) {
        val player = event.enchanter

        val level = AbilityUtil.getPlayerLevel<EnchantingSkill>(player)
        val reduction = AbilityUtil.calculateScaledValue(
            level, MANA_POOL_MIN_LEVEL, maxValue = MANA_POOL_MAX_VALUE
        )

        if (reduction > 0.0) {
            val reducedCost = (event.expLevelCost * (1.0 - reduction)).toInt().coerceAtLeast(1)
            event.expLevelCost = reducedCost
        }
    }
}
