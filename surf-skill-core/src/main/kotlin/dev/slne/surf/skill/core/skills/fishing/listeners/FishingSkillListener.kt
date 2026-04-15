package dev.slne.surf.skill.core.skills.fishing.listeners

import dev.slne.surf.api.core.rarity.Rarity
import dev.slne.surf.enchantment.api.enchantment.EnchantmentManager
import dev.slne.surf.skill.api.SkillInstance
import dev.slne.surf.skill.api.player.incrementExperience
import dev.slne.surf.skill.api.player.skillPlayer
import dev.slne.surf.skill.api.skills.FishingSkill
import dev.slne.surf.skill.core.skills.enchanting.EnchantmentRarityMap
import org.bukkit.Material
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
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

        var expToGive = fishingDropExpMap[item.type] ?: 1

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


    private val fishingDropExpMap = mapOf(
        Material.BOW to 3,
        Material.ENCHANTED_BOOK to 10,
        Material.FISHING_ROD to 3,
        Material.NAME_TAG to 5,
        Material.NAUTILUS_SHELL to 2,
        Material.SADDLE to 2,
    )
}