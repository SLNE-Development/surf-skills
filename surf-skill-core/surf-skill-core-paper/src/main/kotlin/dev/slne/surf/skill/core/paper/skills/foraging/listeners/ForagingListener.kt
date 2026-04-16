@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.paper.skills.foraging.listeners

import dev.slne.surf.enchantment.api.enchantments.replenish.ReplenishBlockEvent
import dev.slne.surf.enchantment.api.enchantments.replenish.ReplenishEnchantment
import dev.slne.surf.enchantment.api.enchantments.telekinesis.PostTelekinesisItemEvent
import dev.slne.surf.enchantment.api.utils.hasCustomEnchantment
import dev.slne.surf.skill.api.paper.SkillInstance
import dev.slne.surf.skill.api.paper.Skills.ForagingSkill
import dev.slne.surf.skill.api.player.incrementExperience
import dev.slne.surf.skill.api.player.skillPlayer
import dev.slne.surf.skill.core.paper.skills.utils.BlockExperienceHandler
import io.papermc.paper.event.block.PlayerShearBlockEvent
import org.bukkit.block.BlockType
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.player.PlayerHarvestBlockEvent
import org.bukkit.event.player.PlayerShearEntityEvent
import org.bukkit.inventory.ItemType

object ForagingListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPostTelekinesis(event: PostTelekinesisItemEvent) {
        if (event.isCancelled) return

        val totalExperience = event.itemStack.amount
        if (totalExperience <= 0) return

        SkillInstance.launch {
            event.player.skillPlayer().incrementExperience<ForagingSkill>(totalExperience)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onShearBlock(event: PlayerShearBlockEvent) {
        if (event.isCancelled) return

        val totalExperience = event.drops.sumOf { it.amount }
        if (totalExperience <= 0) return

        SkillInstance.launch {
            event.player.skillPlayer().incrementExperience<ForagingSkill>(totalExperience)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onShearEntity(event: PlayerShearEntityEvent) {
        if (event.isCancelled) return

        val totalExperience = event.drops.sumOf { it.amount }
        if (totalExperience <= 0) return

        SkillInstance.launch {
            event.player.skillPlayer().incrementExperience<ForagingSkill>(totalExperience)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onHarvestBlock(event: PlayerHarvestBlockEvent) {
        if (event.isCancelled) return

        val totalExperience = event.itemsHarvested.sumOf { it.amount }
        if (totalExperience <= 0) return

        SkillInstance.launch {
            event.player.skillPlayer().incrementExperience<ForagingSkill>(totalExperience)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onFarmlandHarvest(event: BlockDropItemEvent) {
        if (event.isCancelled) return

        val player = event.player
        if (player.inventory.itemInMainHand.hasCustomEnchantment<ReplenishEnchantment>()) return

        val data = event.blockState.blockData
        if (data !is Ageable || data.age < data.maximumAge) return

        val totalExperience = event.items.sumOf { it.itemStack.amount }
        if (totalExperience <= 0) return

        SkillInstance.launch {
            player.skillPlayer().incrementExperience<ForagingSkill>(totalExperience)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onReplenish(event: ReplenishBlockEvent) {
        if (event.isCancelled) return

        val totalExperience = event.items.sumOf { it.itemStack.amount }
        if (totalExperience <= 0) return

        SkillInstance.launch {
            event.player.skillPlayer().incrementExperience<ForagingSkill>(totalExperience)
        }
    }

    private enum class ForagingMap(
        val itemTypes: List<ItemType>,
        val originBlocks: List<BlockType>,
        val experience: Int,
    ) {
        FLOWERS(
            itemTypes = listOf(
                ItemType.DANDELION,
                ItemType.POPPY,
                ItemType.BLUE_ORCHID,
                ItemType.ALLIUM,
                ItemType.AZURE_BLUET,
                ItemType.RED_TULIP,
                ItemType.ORANGE_TULIP,
                ItemType.WHITE_TULIP,
                ItemType.PINK_TULIP,
                ItemType.OXEYE_DAISY,
                ItemType.CORNFLOWER,
                ItemType.LILY_OF_THE_VALLEY,
                ItemType.CACTUS_FLOWER,
                ItemType.OPEN_EYEBLOSSOM,
                ItemType.CLOSED_EYEBLOSSOM,
                ItemType.SUNFLOWER,
                ItemType.LILAC,
                ItemType.ROSE_BUSH,
                ItemType.PEONY,
                ItemType.BIG_DRIPLEAF,
                ItemType.SMALL_DRIPLEAF,
                ItemType.LILY_PAD,
                ItemType.SEA_PICKLE
            ),
            originBlocks = listOf(
                BlockType.DANDELION,
                BlockType.POPPY,
                BlockType.BLUE_ORCHID,
                BlockType.ALLIUM,
                BlockType.AZURE_BLUET,
                BlockType.RED_TULIP,
                BlockType.ORANGE_TULIP,
                BlockType.WHITE_TULIP,
                BlockType.PINK_TULIP,
                BlockType.OXEYE_DAISY,
                BlockType.CORNFLOWER,
                BlockType.LILY_OF_THE_VALLEY,
                BlockType.CACTUS_FLOWER,
                BlockType.OPEN_EYEBLOSSOM,
                BlockType.CLOSED_EYEBLOSSOM,
                BlockType.SUNFLOWER,
                BlockType.LILAC,
                BlockType.ROSE_BUSH,
                BlockType.PEONY,
                BlockType.BIG_DRIPLEAF,
                BlockType.SMALL_DRIPLEAF,
                BlockType.LILY_PAD,
                BlockType.SEA_PICKLE
            ),
            experience = 1
        ),
        SHROOMS(
            itemTypes = listOf(ItemType.RED_MUSHROOM, ItemType.BROWN_MUSHROOM),
            originBlocks = listOf(
                BlockType.BROWN_MUSHROOM,
                BlockType.RED_MUSHROOM,
                BlockType.MUSHROOM_STEM,
                BlockType.RED_MUSHROOM_BLOCK,
                BlockType.BROWN_MUSHROOM_BLOCK
            ),
            experience = 1
        ),
        PRODUCE(
            itemTypes = listOf(
                ItemType.PUMPKIN,
                ItemType.MELON,
                ItemType.MELON_SLICE
            ),
            originBlocks = listOf(
                BlockType.PUMPKIN,
                BlockType.MELON
            ),
            experience = 2,
        ),
        SEEDS(
            itemTypes = listOf(ItemType.WHEAT_SEEDS),
            originBlocks = listOf(BlockType.SHORT_GRASS, BlockType.TALL_GRASS),
            experience = 1
        );

        companion object {
            fun get(itemType: ItemType, originBlock: BlockType) = entries
                .firstOrNull { itemType in it.itemTypes && originBlock in it.originBlocks }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockBreak(event: BlockDropItemEvent) {
        if (event.isCancelled) return

        BlockExperienceHandler.handleBlockDropItem<ForagingSkill>(event) { itemType, originBlock ->
            ForagingMap.get(itemType, originBlock)?.experience ?: 0
        }
    }
}