package dev.slne.surf.skill.core.paper.skills.combat.listeners

import dev.slne.surf.api.core.util.random
import dev.slne.surf.skill.api.paper.skills.CombatSkill
import dev.slne.surf.skill.core.paper.ability.AbilityUtil
import org.bukkit.Material
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.loot.LootContext

object CombatAbilityListener : Listener {
    private const val BATTLE_HARDENED_MIN_LEVEL = 1
    private const val BATTLE_HARDENED_MAX_VALUE = 0.50

    private val WEAPON_TYPES = setOf(
        Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD,
        Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD,
        Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE,
        Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE,
        Material.COPPER_AXE, Material.COPPER_SWORD,
        Material.BOW, Material.CROSSBOW, Material.TRIDENT, Material.MACE,
        Material.COPPER_SPEAR,
        Material.GOLDEN_SPEAR,
        Material.DIAMOND_SPEAR,
        Material.WOODEN_SPEAR,
        Material.STONE_SPEAR,
        Material.IRON_SPEAR,
        Material.NETHERITE_SPEAR
    )

    private val ARMOR_TYPES = setOf(
        Material.LEATHER_HELMET,
        Material.LEATHER_CHESTPLATE,
        Material.LEATHER_LEGGINGS,
        Material.LEATHER_BOOTS,
        Material.CHAINMAIL_HELMET,
        Material.CHAINMAIL_CHESTPLATE,
        Material.CHAINMAIL_LEGGINGS,
        Material.CHAINMAIL_BOOTS,
        Material.COPPER_HELMET,
        Material.COPPER_CHESTPLATE,
        Material.COPPER_LEGGINGS,
        Material.COPPER_BOOTS,
        Material.IRON_HELMET,
        Material.IRON_CHESTPLATE,
        Material.IRON_LEGGINGS,
        Material.IRON_BOOTS,
        Material.GOLDEN_HELMET,
        Material.GOLDEN_CHESTPLATE,
        Material.GOLDEN_LEGGINGS,
        Material.GOLDEN_BOOTS,
        Material.DIAMOND_HELMET,
        Material.DIAMOND_CHESTPLATE,
        Material.DIAMOND_LEGGINGS,
        Material.DIAMOND_BOOTS,
        Material.NETHERITE_HELMET,
        Material.NETHERITE_CHESTPLATE,
        Material.NETHERITE_LEGGINGS,
        Material.NETHERITE_BOOTS,
        Material.TURTLE_HELMET,
        Material.SHIELD,
        Material.ELYTRA
    )

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBattleHardenedDurability(event: PlayerItemDamageEvent) {
        val player = event.player
        val itemType = event.item.type

        if (itemType !in WEAPON_TYPES && itemType !in ARMOR_TYPES) return

        val level = AbilityUtil.getPlayerLevel<CombatSkill>(player)
        val reductionChance = AbilityUtil.calculateScaledValue(
            level, BATTLE_HARDENED_MIN_LEVEL, maxValue = BATTLE_HARDENED_MAX_VALUE
        )

        if (AbilityUtil.rollChance(reductionChance)) {
            event.damage = 0
        }
    }

    private const val REAPERS_FORTUNE_MIN_LEVEL = 11
    private const val REAPERS_FORTUNE_MAX_VALUE = 0.20

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onReapersFortune(event: EntityDeathEvent) {
        val entity = event.entity
        if (entity !is Monster) {
            return
        }

        val killer = getKiller(event) ?: return

        val level = AbilityUtil.getPlayerLevel<CombatSkill>(killer)
        val chance = AbilityUtil.calculateScaledValue(
            level,
            REAPERS_FORTUNE_MIN_LEVEL,
            maxValue = REAPERS_FORTUNE_MAX_VALUE
        )

        if (AbilityUtil.rollChance(chance)) {
            val items = event.drops.toList()

            event.drops += items
        }
    }

    private const val STRONG_IMPACT_MIN_LEVEL = 21
    private const val STRONG_IMPACT_MAX_VALUE = 0.15

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onStrongImpact(event: EntityDamageByEntityEvent) {
        val target = event.entity
        if (target !is Monster) return

        val player = when (val damager = event.damager) {
            is Player -> damager
            is Projectile -> damager.shooter as? Player
            else -> null
        } ?: return

        val level = AbilityUtil.getPlayerLevel<CombatSkill>(player)
        val damageBonus = AbilityUtil.calculateScaledValue(
            level, STRONG_IMPACT_MIN_LEVEL, maxValue = STRONG_IMPACT_MAX_VALUE
        )

        if (damageBonus > 0.0) {
            event.damage *= (1.0 + damageBonus)
        }
    }

    private fun getKiller(event: EntityDeathEvent): Player? {
        val killer = event.entity.killer
        if (killer != null) return killer

        val lastDamage = event.entity.lastDamageCause
        if (lastDamage is EntityDamageByEntityEvent) {
            val damager = lastDamage.damager
            if (damager is Player) return damager
            if (damager is Projectile) return damager.shooter as? Player
        }

        return null
    }
}
