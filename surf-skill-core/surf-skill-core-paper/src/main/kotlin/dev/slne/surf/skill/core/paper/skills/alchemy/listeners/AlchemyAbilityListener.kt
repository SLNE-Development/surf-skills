package dev.slne.surf.skill.core.paper.skills.alchemy.listeners

import dev.slne.surf.skill.api.paper.skills.AlchemySkill
import dev.slne.surf.skill.core.paper.ability.AbilityUtil
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object AlchemyAbilityListener : Listener {
    private val processingPotency = ConcurrentHashMap.newKeySet<UUID>()
    private const val RESILIENCE_MIN_LEVEL = 1
    private const val RESILIENCE_MAX_VALUE = 0.30

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onAlchemistsResilience(event: EntityDamageEvent) {
        val player = event.entity as? Player ?: return

        val cause = event.cause
        if (cause != EntityDamageEvent.DamageCause.POISON &&
            cause != EntityDamageEvent.DamageCause.WITHER &&
            cause != EntityDamageEvent.DamageCause.MAGIC
        ) return

        val level = AbilityUtil.getPlayerLevel<AlchemySkill>(player)
        val reduction = AbilityUtil.calculateScaledValue(
            level, RESILIENCE_MIN_LEVEL, maxValue = RESILIENCE_MAX_VALUE
        )

        if (reduction > 0.0) {
            event.damage *= (1.0 - reduction)
        }
    }

    private const val RECYCLER_MIN_LEVEL = 11
    private const val RECYCLER_MAX_VALUE = 0.20

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPotionRecycler(event: PlayerItemConsumeEvent) {
        val player = event.player
        val item = event.item

        if (item.type != Material.POTION) return

        val level = AbilityUtil.getPlayerLevel<AlchemySkill>(player)
        val chance = AbilityUtil.calculateScaledValue(
            level, RECYCLER_MIN_LEVEL, maxValue = RECYCLER_MAX_VALUE
        )

        if (AbilityUtil.rollChance(chance)) {
            event.replacement = item.clone()
        }
    }

    private const val POTENCY_MIN_LEVEL = 21
    private const val POTENCY_MAX_VALUE = 0.50

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBrewPotency(event: EntityPotionEffectEvent) {
        val player = event.entity as? Player ?: return

        if (!processingPotency.add(player.uniqueId)) return

        try {
            if (event.action != EntityPotionEffectEvent.Action.ADDED) return

            val newEffect = event.newEffect ?: return

            if (!isPositiveEffect(newEffect.type)) return

            val level = AbilityUtil.getPlayerLevel<AlchemySkill>(player)
            val durationBonus = AbilityUtil.calculateScaledValue(
                level, POTENCY_MIN_LEVEL, maxValue = POTENCY_MAX_VALUE
            )

            if (durationBonus <= 0.0) return

            val extendedDuration = (newEffect.duration * (1.0 + durationBonus)).toInt()
            event.isCancelled = true

            player.addPotionEffect(
                PotionEffect(
                    newEffect.type,
                    extendedDuration,
                    newEffect.amplifier,
                    newEffect.isAmbient,
                    newEffect.hasParticles(),
                    newEffect.hasIcon()
                )
            )
        } finally {
            processingPotency.remove(player.uniqueId)
        }
    }

    private fun isPositiveEffect(type: PotionEffectType): Boolean {
        return type == PotionEffectType.SPEED ||
                type == PotionEffectType.HASTE ||
                type == PotionEffectType.STRENGTH ||
                type == PotionEffectType.INSTANT_HEALTH ||
                type == PotionEffectType.JUMP_BOOST ||
                type == PotionEffectType.REGENERATION ||
                type == PotionEffectType.RESISTANCE ||
                type == PotionEffectType.FIRE_RESISTANCE ||
                type == PotionEffectType.WATER_BREATHING ||
                type == PotionEffectType.INVISIBILITY ||
                type == PotionEffectType.NIGHT_VISION ||
                type == PotionEffectType.ABSORPTION ||
                type == PotionEffectType.SATURATION ||
                type == PotionEffectType.LUCK ||
                type == PotionEffectType.SLOW_FALLING ||
                type == PotionEffectType.CONDUIT_POWER ||
                type == PotionEffectType.HERO_OF_THE_VILLAGE
    }
}
