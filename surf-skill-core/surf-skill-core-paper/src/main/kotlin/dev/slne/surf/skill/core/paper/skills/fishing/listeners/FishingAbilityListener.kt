package dev.slne.surf.skill.core.paper.skills.fishing.listeners

import dev.slne.surf.skill.api.paper.skills.FishingSkill
import dev.slne.surf.skill.core.paper.ability.AbilityUtil
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityAirChangeEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerFishEvent

object FishingAbilityListener : Listener {
    private const val MAGNETIC_ROD_MIN_LEVEL = 1
    private const val MAGNETIC_ROD_MAX_VALUE = 0.25

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onMagneticRod(event: PlayerFishEvent) {
        if (event.state != PlayerFishEvent.State.FISHING) return

        val player = event.player
        val hook = event.hook

        val level = AbilityUtil.getPlayerLevel<FishingSkill>(player)
        val speedBonus = AbilityUtil.calculateScaledValue(
            level, MAGNETIC_ROD_MIN_LEVEL, maxValue = MAGNETIC_ROD_MAX_VALUE
        )

        if (speedBonus <= 0.0) return

        val reductionFactor = 1.0 - speedBonus
        hook.minWaitTime = (hook.minWaitTime * reductionFactor).toInt().coerceAtLeast(1)
        hook.maxWaitTime =
            (hook.maxWaitTime * reductionFactor).toInt().coerceAtLeast(hook.minWaitTime + 1)
    }

    private const val NEPTUNES_FAVOR_MIN_LEVEL = 11
    private const val NEPTUNES_FAVOR_MAX_VALUE = 0.20

    private val WATER_CREATURES = setOf(
        EntityType.COD, EntityType.SALMON, EntityType.TROPICAL_FISH,
        EntityType.PUFFERFISH, EntityType.SQUID, EntityType.GLOW_SQUID,
        EntityType.DOLPHIN, EntityType.TURTLE, EntityType.GUARDIAN,
        EntityType.ELDER_GUARDIAN, EntityType.DROWNED
    )

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onNeptunesFavor(event: EntityDeathEvent) {
        val entity = event.entity
        if (entity.type !in WATER_CREATURES) return

        val killer = getKiller(event) ?: return

        val level = AbilityUtil.getPlayerLevel<FishingSkill>(killer)
        val chance = AbilityUtil.calculateScaledValue(
            level, NEPTUNES_FAVOR_MIN_LEVEL, maxValue = NEPTUNES_FAVOR_MAX_VALUE
        )

        if (AbilityUtil.rollChance(chance)) {
            val extraDrops = event.drops.map { it.clone() }
            extraDrops.forEach { drop ->
                entity.world.dropItemNaturally(entity.location, drop)
            }
        }
    }

    private const val BIGGER_LUNGS_MIN_LEVEL = 21
    private const val BIGGER_LUNGS_MAX_VALUE = 3.00

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBiggerLungs(event: EntityAirChangeEvent) {
        val player = event.entity as? Player ?: return

        val oldAir = player.remainingAir
        val newAir = event.amount

        if (newAir >= oldAir) return

        val level = AbilityUtil.getPlayerLevel<FishingSkill>(player)
        val breathingBonus = AbilityUtil.calculateScaledValue(
            level, BIGGER_LUNGS_MIN_LEVEL, maxValue = BIGGER_LUNGS_MAX_VALUE
        )

        if (breathingBonus <= 0.0) return

        val airLoss = oldAir - newAir
        val reducedLoss = (airLoss / (1.0 + breathingBonus)).toInt().coerceAtLeast(0)
        event.amount = oldAir - reducedLoss
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
