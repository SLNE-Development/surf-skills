package dev.slne.surf.skill.core.skills.enchanting.listeners

import dev.slne.surf.api.core.rarity.Rarity
import dev.slne.surf.enchantment.api.enchantment.EnchantmentManager
import dev.slne.surf.skill.api.SkillInstance
import dev.slne.surf.skill.api.player.incrementExperience
import dev.slne.surf.skill.api.player.skillPlayer
import dev.slne.surf.skill.api.skills.EnchantingSkill
import dev.slne.surf.skill.core.skills.enchanting.EnchantmentRarityMap
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType

object EnchantListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onEnchant(event: EnchantItemEvent) {
        if (event.isCancelled) return

        val player = event.enchanter
        val enchantments = event.enchantsToAdd.toList().associateWith {
            EnchantmentManager.findByBukkitEnchantment(it.first)?.rarity ?: Rarity.COMMON
        }

        SkillInstance.launch {
            val skillPlayer = player.skillPlayer()
            val totalExperience = enchantments.map { (enchantmentLevel, rarity) ->
                val (_, level) = enchantmentLevel
                val rarityMap = EnchantmentRarityMap.getByEnchantmentRarity(rarity)

                rarityMap.experience + level
            }.sum()

            skillPlayer.incrementExperience<EnchantingSkill>(totalExperience)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onGrindstone(event: InventoryClickEvent) {
        if (event.isCancelled) return

        val inventoryType = event.clickedInventory?.type ?: return
        if (inventoryType != InventoryType.GRINDSTONE && inventoryType != InventoryType.ANVIL) return

        val player = event.whoClicked as? Player ?: return

        val clickedSlot = event.slotType
        if (clickedSlot != InventoryType.SlotType.RESULT) return

        val clickedItem = event.currentItem ?: event.cursor
        if (clickedItem.type == Material.AIR) return

        SkillInstance.launch {
            player.skillPlayer().incrementExperience<EnchantingSkill>(1)
        }
    }
}