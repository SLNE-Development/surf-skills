package dev.slne.surf.skill.core.paper.skills.combat.listeners

import dev.slne.surf.skill.api.paper.SkillInstance
import dev.slne.surf.skill.api.paper.player.SkillPlayerManager
import dev.slne.surf.skill.api.paper.player.incrementExperience
import dev.slne.surf.skill.api.paper.skills.CombatSkill
import dev.slne.surf.skill.core.paper.util.SkillLevelingHandler
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent

object CombatKillListener : Listener {
    private val xpMap = mapOf(
        EntityType.ENDER_DRAGON to 1000,
        EntityType.WITHER to 500,
        EntityType.ALLAY to 500,
        EntityType.WARDEN to 400,

        EntityType.EVOKER to 50,
        EntityType.VINDICATOR to 50,
        EntityType.ILLUSIONER to 50,
        EntityType.RAVAGER to 50,
        EntityType.WITHER_SKELETON to 50,
        EntityType.PIGLIN_BRUTE to 50,
        EntityType.ZOGLIN to 50,
        EntityType.GHAST to 50,
        EntityType.BLAZE to 50,
        EntityType.BREEZE to 50,
        EntityType.SHULKER to 50,
        EntityType.ZOMBIE_HORSE to 50,

        EntityType.ENDERMAN to 30,
        EntityType.PHANTOM to 30,
        EntityType.DROWNED to 30,
        EntityType.HUSK to 30,
        EntityType.STRAY to 30,
        EntityType.PILLAGER to 30,
        EntityType.CAVE_SPIDER to 30,
        EntityType.ZOMBIE_VILLAGER to 30,
        EntityType.ZOMBIFIED_PIGLIN to 30,
        EntityType.PIGLIN to 30,
        EntityType.HOGLIN to 30,
        EntityType.VEX to 30,

        EntityType.ZOMBIE to 20,
        EntityType.SKELETON to 20,
        EntityType.SPIDER to 20,
        EntityType.WITCH to 20,

        EntityType.GUARDIAN to 10,
        EntityType.WOLF to 10,
        EntityType.POLAR_BEAR to 10,
        EntityType.FOX to 10,
        EntityType.CAT to 10,
        EntityType.SKELETON_HORSE to 10,
        EntityType.PANDA to 10,
        EntityType.STRIDER to 10,
        EntityType.IRON_GOLEM to 10,
        EntityType.SNOW_GOLEM to 10,
        EntityType.CREEPER to 10,
        EntityType.PARCHED to 20,
        EntityType.BOGGED to 20,
        EntityType.CAMEL_HUSK to 40,
        EntityType.NAUTILUS to 5,
        EntityType.ZOMBIE_NAUTILUS to 10,


        EntityType.BAT to 5,
        EntityType.VILLAGER to 5,

        EntityType.MAGMA_CUBE to 1,
        EntityType.SLIME to 1,
        EntityType.SULFUR_CUBE to 1,
        EntityType.ENDERMITE to 1,
        EntityType.SILVERFISH to 1
    )

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onKill(event: EntityDeathEvent) {
        var killer = event.entity.killer
        val entity = event.entity
        val lastDamageEvent = entity.lastDamageCause

        if (killer == null && lastDamageEvent == null) {
            return
        }

        if (killer == null) {
            val lastDamageEvent = entity.lastDamageCause!!

            if (lastDamageEvent is EntityDamageByEntityEvent) {
                killer = handleEntityDamageByEntityEvent(lastDamageEvent)
            }
        }

        if (killer == null) return
        val uuid = killer.uniqueId

        val xp = xpMap[entity.type] ?: return

        if (!SkillLevelingHandler.canCollectExperience(killer, CombatSkill)) {
            return
        }

        SkillInstance.launch {
            val skillPlayer = SkillPlayerManager.fetchOrCreatePlayer(uuid)

            skillPlayer.incrementExperience<CombatSkill>(xp)
        }
    }

    private fun handleEntityDamageByEntityEvent(event: EntityDamageByEntityEvent): Player? {
        val entity = event.damager

        if (entity is Player) return entity
        if (entity is Projectile) return handleProjectile(entity)

        return null
    }

    private fun handleProjectile(projectile: Projectile): Player? {
        val shooter = projectile.shooter ?: return null

        if (shooter is Player) return shooter

        return null
    }
}