package dev.slne.surf.skill.core.paper.skills.fishing.listeners

import dev.slne.surf.api.core.rarity.Rarity
import dev.slne.surf.enchantment.api.enchantment.EnchantmentManager
import dev.slne.surf.skill.api.paper.SkillInstance
import dev.slne.surf.skill.api.paper.player.SkillPlayerManager
import dev.slne.surf.skill.api.paper.player.incrementExperience
import dev.slne.surf.skill.api.paper.player.skillPlayer
import dev.slne.surf.skill.api.paper.skills.FishingSkill
import dev.slne.surf.skill.api.paper.skills.ForagingSkill
import dev.slne.surf.skill.core.paper.skills.enchanting.EnchantmentRarityMap
import dev.slne.surf.skill.core.paper.util.SkillLevelingHandler
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.inventory.meta.EnchantmentStorageMeta

object FishingSkillListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onFish(event: PlayerFishEvent) {
        if (event.isCancelled) {
            return
        }

        if (event.state != PlayerFishEvent.State.CAUGHT_FISH) {
            return
        }

        val caught = event.caught ?: return

        if (caught !is Item) {
            return
        }

        val item = caught.itemStack
        val meta = item.itemMeta ?: return

        if (!SkillLevelingHandler.canCollectExperience(event.player, FishingSkill)) {
            return
        }

        var expToGive = fishingDropExpMap[item.type] ?: 1

        expToGive = (expToGive * event.expToDrop * 3)

        if (meta is EnchantmentStorageMeta) {
            val enchantments = meta.storedEnchants.toList().associateWith {
                EnchantmentManager.findByBukkitEnchantment(it.first)?.rarity ?: Rarity.COMMON
            }

            expToGive += enchantments.map { (enchantmentLevel, rarity) ->
                val (_, level) = enchantmentLevel
                val rarityMap = EnchantmentRarityMap.getByEnchantmentRarity(rarity)

                rarityMap.experience + level
            }.sum()
        }

        SkillInstance.launch {
            event.player.skillPlayer().incrementExperience<FishingSkill>(expToGive)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onKill(event: EntityDeathEvent) {
        val killer = event.entity.killer ?: return
        val entity = event.entity

        val uuid = killer.uniqueId

        if (!SkillLevelingHandler.canCollectExperience(killer, ForagingSkill)) {
            return
        }

        val xp = killEntities[entity.type] ?: return

        SkillInstance.launch {
            val skillPlayer = SkillPlayerManager.fetchOrCreatePlayer(uuid)

            skillPlayer.incrementExperience<ForagingSkill>(
                xp
            )
        }
    }

    private val killEntities = mapOf(
        EntityType.SALMON to 10,
        EntityType.COD to 10,
        EntityType.TROPICAL_FISH to 10,
        EntityType.GLOW_SQUID to 10,
        EntityType.SQUID to 10,
        EntityType.PUFFERFISH to 20,
        EntityType.DOLPHIN to 40,
        EntityType.ELDER_GUARDIAN to 50
    )

    private val fishingDropExpMap = mapOf(
        Material.BOW to 3,
        Material.ENCHANTED_BOOK to 10,
        Material.FISHING_ROD to 3,
        Material.NAME_TAG to 5,
        Material.NAUTILUS_SHELL to 2,
        Material.SADDLE to 2,
    )
}