@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.paper.skills.foraging.listeners

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.enchantment.api.enchantments.replenish.ReplenishBlockEvent
import dev.slne.surf.enchantment.api.enchantments.replenish.ReplenishEnchantment
import dev.slne.surf.enchantment.api.utils.hasCustomEnchantment
import dev.slne.surf.skill.api.paper.SkillInstance
import dev.slne.surf.skill.api.paper.player.SkillPlayerManager
import dev.slne.surf.skill.api.paper.player.incrementExperience
import dev.slne.surf.skill.api.paper.player.skillPlayer
import dev.slne.surf.skill.api.paper.skills.ForagingSkill
import dev.slne.surf.skill.core.paper.util.BlockExperienceHandler
import dev.slne.surf.skill.core.paper.util.SkillLevelingHandler
import io.papermc.paper.event.block.PlayerShearBlockEvent
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockState
import org.bukkit.block.data.Ageable
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.entity.EntityBreedEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerHarvestBlockEvent
import org.bukkit.event.player.PlayerShearEntityEvent
import kotlin.time.Duration.Companion.seconds

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

        val killEntities = mapOf(
            EntityType.COW to 5,
            EntityType.PIG to 5,
            EntityType.SHEEP to 5,
            EntityType.CHICKEN to 5,
            EntityType.RABBIT to 5,
            EntityType.OCELOT to 5,
            EntityType.BEE to 5,
            EntityType.AXOLOTL to 5,
            EntityType.TADPOLE to 5,
            EntityType.FROG to 5,
            EntityType.CAMEL to 5,
            EntityType.SNIFFER to 5,
            EntityType.ARMADILLO to 5,
            EntityType.LLAMA to 10,
            EntityType.TRADER_LLAMA to 10,
            EntityType.MULE to 10,
            EntityType.HORSE to 10,
            EntityType.DONKEY to 10,
            EntityType.PARROT to 10,
            EntityType.TURTLE to 10,
            EntityType.GOAT to 10,
            EntityType.MOOSHROOM to 10
        )
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onKill(event: EntityDeathEvent) {
        val killer = event.entity.killer ?: return
        val entity = event.entity

        val uuid = killer.uniqueId

        if (!SkillLevelingHandler.canCollectExperience(killer, ForagingSkill)) {
            return
        }

        val xp = ForagingXp.killEntities[entity.type] ?: return

        SkillInstance.launch {
            val skillPlayer = SkillPlayerManager.fetchOrCreatePlayer(uuid)

            skillPlayer.incrementExperience<ForagingSkill>(
                xp
            )
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onShearBlock(event: PlayerShearBlockEvent) {
        if (event.isCancelled) return

        if (!SkillLevelingHandler.canCollectExperience(event.player, ForagingSkill)) {
            return
        }

        val xp = event.drops.sumOf { it.amount }
        if (xp <= 0) return

        SkillInstance.launch {
            event.player.skillPlayer().incrementExperience<ForagingSkill>(xp)
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onShearEntity(event: PlayerShearEntityEvent) {
        if (event.isCancelled) return

        if (!SkillLevelingHandler.canCollectExperience(event.player, ForagingSkill)) {
            return
        }

        val xp = ForagingXp.shearEntity[event.entity.type] ?: return

        SkillInstance.launch {
            event.player.skillPlayer().incrementExperience<ForagingSkill>(xp)
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onHarvestBlock(event: PlayerHarvestBlockEvent) {
        if (event.isCancelled) return

        if (!SkillLevelingHandler.canCollectExperience(event.player, ForagingSkill)) {
            return
        }

        if (!event.harvestedBlock.isFullyGrown()) {
            return
        }

        val xp = ForagingXp.click[event.harvestedBlock.type]
            ?: event.itemsHarvested.sumOf { it.amount }

        if (xp <= 0) return

        SkillInstance.launch {
            event.player.skillPlayer().incrementExperience<ForagingSkill>(xp)
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onBlockDropAgeable(event: BlockDropItemEvent) {
        if (event.isCancelled) {
            return
        }

        if (!SkillLevelingHandler.canCollectExperience(event.player, ForagingSkill)) {
            return
        }

        if (!event.blockState.isFullyGrown()) {
            return
        }

        if (event.blockState.type == Material.MANGROVE_PROPAGULE) {
            return
        }

        val player = event.player
        if (player.inventory.itemInMainHand.hasCustomEnchantment<ReplenishEnchantment>()) return

        val data = event.blockState.blockData
        if (data !is Ageable || data.age < data.maximumAge) return

        val xp = ForagingXp.mine[event.blockState.type] ?: event.items.sumOf { it.itemStack.amount }
        if (xp <= 0) return

        SkillInstance.launch {
            player.skillPlayer().incrementExperience<ForagingSkill>(xp)
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onReplenish(event: ReplenishBlockEvent) {
        if (event.isCancelled) return

        if (!event.blockState.isFullyGrown()) {
            return
        }

        if (!SkillLevelingHandler.canCollectExperience(event.player, ForagingSkill)) {
            return
        }

        val xp = ForagingXp.mine[event.blockState.type] ?: event.items.sumOf { it.itemStack.amount }
        if (xp <= 0) return

        SkillInstance.launch {
            event.player.skillPlayer().incrementExperience<ForagingSkill>(xp)
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onBlockBreak(event: BlockBreakEvent) {
        if (event.isCancelled) return

        if (!SkillLevelingHandler.canCollectExperience(event.player, ForagingSkill)) {
            return
        }

        locationsCache.put(
            event.block.location,
            BlockExperienceHandler.isEligibleForExperience(event.block)
        )
    }

    private val locationsCache = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterWrite(1.seconds)
        .build<Location, Boolean>()

    @EventHandler(priority = EventPriority.HIGH)
    fun onBlockDrop(event: BlockDropItemEvent) {
        if (event.isCancelled) return

        if (!event.blockState.isFullyGrown()) {
            return
        }

        if (!SkillLevelingHandler.canCollectExperience(event.player, ForagingSkill)) {
            return
        }

        if (locationsCache.getIfPresent(event.block.location) == false) {
            return
        }

        val xp = ForagingXp.mine[event.blockState.type] ?: return
        if (xp <= 0) return

        SkillInstance.launch {
            event.player.skillPlayer().incrementExperience<ForagingSkill>(xp)
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onBreed(event: EntityBreedEvent) {
        if (event.isCancelled) return

        val breeder = event.breeder as? Player ?: return

        if (!SkillLevelingHandler.canCollectExperience(breeder, ForagingSkill)) {
            return
        }

        SkillInstance.launch {
            breeder.skillPlayer().incrementExperience<ForagingSkill>(event.experience * 10)
        }
    }

    fun Block.isFullyGrown(): Boolean {
        val data = blockData
        return data !is Ageable || data.age >= data.maximumAge
    }

    fun Block.isFullyGrownIfAgable() =
        blockData !is Ageable || (blockData as Ageable).age >= (blockData as Ageable).maximumAge

    fun BlockState.isFullyGrown(): Boolean {
        val data = blockData
        return data !is Ageable || data.age >= data.maximumAge
    }
}