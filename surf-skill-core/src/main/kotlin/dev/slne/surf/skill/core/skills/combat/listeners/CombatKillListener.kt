package dev.slne.surf.skill.core.skills.combat.listeners

import dev.slne.surf.skill.api.SkillInstance
import dev.slne.surf.skill.api.player.SkillPlayerManager
import dev.slne.surf.skill.api.player.incrementExperience
import dev.slne.surf.skill.api.skills.CombatSkill
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent

object CombatKillListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
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

        SkillInstance.launch {
            val skillPlayer = SkillPlayerManager.fetchOrCreatePlayer(uuid)

            skillPlayer.incrementExperience<CombatSkill>(1)
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