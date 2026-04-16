package dev.slne.surf.skill.core.skills.exploration.listeners

import dev.slne.surf.skill.api.SkillInstance
import dev.slne.surf.skill.api.skills.ExplorationSkill
import dev.slne.surf.skill.core.ability.AbilityUtil
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object ExplorationAbilityListener : Listener {
    private const val ENDURANCE_MIN_LEVEL = 1
    private const val ENDURANCE_MAX_VALUE = 0.50

    private val BOOT_TYPES = setOf(
        Material.LEATHER_BOOTS,
        Material.CHAINMAIL_BOOTS,
        Material.COPPER_BOOTS,
        Material.IRON_BOOTS,
        Material.GOLDEN_BOOTS,
        Material.DIAMOND_BOOTS,
        Material.NETHERITE_BOOTS
    )

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onWanderersEndurance(event: PlayerItemDamageEvent) {
        val player = event.player
        val itemType = event.item.type

        if (itemType !in BOOT_TYPES) return

        val level = AbilityUtil.getPlayerLevel<ExplorationSkill>(player)
        val reductionChance = AbilityUtil.calculateScaledValue(
            level, ENDURANCE_MIN_LEVEL, maxValue = ENDURANCE_MAX_VALUE
        )

        if (AbilityUtil.rollChance(reductionChance)) {
            event.damage = 0
        }
    }

    private const val SWIFT_FEET_MIN_LEVEL = 11
    private const val SWIFT_FEET_MAX_VALUE = 0.10

    private val SPEED_MODIFIER_KEY = NamespacedKey("surf", "skill_exploration_speed")

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        SkillInstance.launch {
            val player = event.player
            if (!player.isOnline) return@launch
            applySpeedModifier(player)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        removeSpeedModifier(event.player)
    }

    fun applySpeedModifier(player: Player) {
        val level = AbilityUtil.getPlayerLevel<ExplorationSkill>(player)
        val speedBonus = AbilityUtil.calculateScaledValue(
            level, SWIFT_FEET_MIN_LEVEL, maxValue = SWIFT_FEET_MAX_VALUE
        )

        val attribute = player.getAttribute(Attribute.MOVEMENT_SPEED) ?: return

        attribute.removeModifier(SPEED_MODIFIER_KEY)

        if (speedBonus > 0.0) {
            attribute.addModifier(
                AttributeModifier(
                    SPEED_MODIFIER_KEY,
                    speedBonus,
                    AttributeModifier.Operation.MULTIPLY_SCALAR_1
                )
            )
        }
    }

    fun removeSpeedModifier(player: Player) {
        val attribute = player.getAttribute(Attribute.MOVEMENT_SPEED) ?: return
        attribute.removeModifier(SPEED_MODIFIER_KEY)
    }

    private const val SAFE_LANDING_MIN_LEVEL = 21
    private const val SAFE_LANDING_MAX_VALUE = 0.40

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onSafeLanding(event: EntityDamageEvent) {
        val player = event.entity as? Player ?: return

        if (event.cause != EntityDamageEvent.DamageCause.FALL) return

        val level = AbilityUtil.getPlayerLevel<ExplorationSkill>(player)
        val reduction = AbilityUtil.calculateScaledValue(
            level, SAFE_LANDING_MIN_LEVEL, maxValue = SAFE_LANDING_MAX_VALUE
        )

        if (reduction > 0.0) {
            event.damage *= (1.0 - reduction)
        }
    }
}
