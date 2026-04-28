@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.paper.skills.foraging.listeners

import dev.slne.surf.enchantment.api.enchantments.replenish.ReplenishBlockEvent
import dev.slne.surf.enchantment.api.enchantments.replenish.ReplenishEnchantment
import dev.slne.surf.enchantment.api.enchantments.telekinesis.PostTelekinesisItemEvent
import dev.slne.surf.enchantment.api.utils.hasCustomEnchantment
import dev.slne.surf.skill.api.paper.SkillInstance
import dev.slne.surf.skill.api.paper.player.incrementExperience
import dev.slne.surf.skill.api.paper.player.skillPlayer
import dev.slne.surf.skill.api.paper.skills.ForagingSkill
import io.papermc.paper.event.block.PlayerShearBlockEvent
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.player.PlayerHarvestBlockEvent
import org.bukkit.event.player.PlayerShearEntityEvent

object ForagingListener : Listener {

    private object ForagingXp {
        val mine: Map<Material, Int> = mapOf(
            Material.BAMBOO to 1,

            Material.CACTUS to 2,
            Material.SUGAR_CANE to 2,

            Material.COCOA to 3,
            Material.CARROTS to 3,
            Material.SWEET_BERRY_BUSH to 3,
            Material.CAVE_VINES_PLANT to 3,

            Material.WHEAT to 4,
            Material.POTATOES to 4,
            Material.BEETROOTS to 4,

            Material.NETHER_WART to 20,
            Material.PUMPKIN to 20,
            Material.MELON to 20
        )

        val click: Map<Material, Int> = mapOf(
            Material.SWEET_BERRY_BUSH to 3,
            Material.CAVE_VINES_PLANT to 3
        )

        val shearEntity: Map<EntityType, Int> = mapOf(
            EntityType.SHEEP to 20
        )
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPostTelekinesis(event: PostTelekinesisItemEvent) {
        if (event.isCancelled) return

        val xp = event.itemStack.amount
        if (xp <= 0) return

        if (!event.player.hasPermission("surf.skill.foraging")) {
            return
        }

        SkillInstance.launch {
            event.player.skillPlayer().incrementExperience<ForagingSkill>(xp)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onShearBlock(event: PlayerShearBlockEvent) {
        if (event.isCancelled) return

        if (!event.player.hasPermission("surf.skill.foraging")) {
            return
        }

        val xp = event.drops.sumOf { it.amount }
        if (xp <= 0) return

        SkillInstance.launch {
            event.player.skillPlayer().incrementExperience<ForagingSkill>(xp)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onShearEntity(event: PlayerShearEntityEvent) {
        if (event.isCancelled) return

        if (!event.player.hasPermission("surf.skill.foraging")) {
            return
        }

        val xp = ForagingXp.shearEntity[event.entity.type] ?: return

        SkillInstance.launch {
            event.player.skillPlayer().incrementExperience<ForagingSkill>(xp)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onHarvestBlock(event: PlayerHarvestBlockEvent) {
        if (event.isCancelled) return

        if (!event.player.hasPermission("surf.skill.foraging")) {
            return
        }

        val xp = ForagingXp.click[event.harvestedBlock.type]
            ?: event.itemsHarvested.sumOf { it.amount }

        if (xp <= 0) return

        SkillInstance.launch {
            event.player.skillPlayer().incrementExperience<ForagingSkill>(xp)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onFarmlandHarvest(event: BlockDropItemEvent) {
        if (event.isCancelled) return

        if (!event.player.hasPermission("surf.skill.foraging")) {
            return
        }

        val player = event.player
        if (player.inventory.itemInMainHand.hasCustomEnchantment<ReplenishEnchantment>()) return

        val data = event.blockState.blockData
        if (data !is Ageable || data.age < data.maximumAge) return

        val xp = event.items.sumOf { it.itemStack.amount }
        if (xp <= 0) return

        SkillInstance.launch {
            player.skillPlayer().incrementExperience<ForagingSkill>(xp)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onReplenish(event: ReplenishBlockEvent) {
        if (event.isCancelled) return

        if (!event.player.hasPermission("surf.skill.foraging")) {
            return
        }

        val xp = event.items.sumOf { it.itemStack.amount }
        if (xp <= 0) return

        SkillInstance.launch {
            event.player.skillPlayer().incrementExperience<ForagingSkill>(xp)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockBreak(event: BlockDropItemEvent) {
        if (event.isCancelled) return

        if (!event.player.hasPermission("surf.skill.foraging")) {
            return
        }

        val xp = ForagingXp.mine[event.block.type] ?: return
        if (xp <= 0) return

        val total = event.items.sumOf { it.itemStack.amount } * xp

        SkillInstance.launch {
            event.player.skillPlayer().incrementExperience<ForagingSkill>(total)
        }
    }
}