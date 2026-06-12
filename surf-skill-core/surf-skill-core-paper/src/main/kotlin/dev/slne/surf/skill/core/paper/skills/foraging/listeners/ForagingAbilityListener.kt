package dev.slne.surf.skill.core.paper.skills.foraging.listeners

import dev.slne.surf.skill.api.paper.skills.ForagingSkill
import dev.slne.surf.skill.core.paper.ability.AbilityUtil
import dev.slne.surf.skill.core.paper.util.isEligibleForExperience
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.data.Ageable
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerItemDamageEvent

object ForagingAbilityListener : Listener {
    private const val EARTHBOUND_DURABILITY_MIN_LEVEL = 1
    private const val EARTHBOUND_DURABILITY_MAX_VALUE = 0.50

    private val SHOVEL_HOE_TYPES = setOf(
        Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL,
        Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL,
        Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE,
        Material.GOLDEN_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE
    )

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onEarthboundDurability(event: PlayerItemDamageEvent) {
        val player = event.player
        val itemType = event.item.type

        if (itemType !in SHOVEL_HOE_TYPES) return

        val level = AbilityUtil.getPlayerLevel<ForagingSkill>(player)
        val reductionChance = AbilityUtil.calculateScaledValue(
            level, EARTHBOUND_DURABILITY_MIN_LEVEL, maxValue = EARTHBOUND_DURABILITY_MAX_VALUE
        )

        if (AbilityUtil.rollChance(reductionChance)) {
            event.damage = 0
        }
    }

    private const val GREEN_THUMB_MIN_LEVEL = 11
    private const val GREEN_THUMB_MAX_VALUE = 0.20

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onGreenThumb(event: BlockDropItemEvent) {
        val data = event.blockState.blockData
        if (data !is Ageable || data.age < data.maximumAge) return
        if (!event.block.isEligibleForExperience()) return

        val player = event.player
        val level = AbilityUtil.getPlayerLevel<ForagingSkill>(player)
        val chance = AbilityUtil.calculateScaledValue(
            level, GREEN_THUMB_MIN_LEVEL, maxValue = GREEN_THUMB_MAX_VALUE
        )

        if (AbilityUtil.rollChance(chance)) {
            event.items.forEach {
                it.world.dropItemNaturally(it.location, it.itemStack.clone())
            }

            player.spawnParticle(Particle.TRIAL_SPAWNER_DETECTION, event.block.location, 5)
        }
    }

    private const val SATIATION_MIN_LEVEL = 21
    private const val SATIATION_MAX_CHANCE = 0.60

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onSatiation(event: FoodLevelChangeEvent) {
        val player = event.entity as? Player ?: return

        val newFoodLevel = event.foodLevel
        val oldFoodLevel = player.foodLevel

        if (newFoodLevel >= oldFoodLevel) return

        val level = AbilityUtil.getPlayerLevel<ForagingSkill>(player)
        val chance = AbilityUtil.calculateScaledValue(
            level, SATIATION_MIN_LEVEL, maxValue = SATIATION_MAX_CHANCE
        )

        if (chance <= 0.0) return

        if (Math.random() <= chance) {
            event.foodLevel = oldFoodLevel
        }
    }
}
