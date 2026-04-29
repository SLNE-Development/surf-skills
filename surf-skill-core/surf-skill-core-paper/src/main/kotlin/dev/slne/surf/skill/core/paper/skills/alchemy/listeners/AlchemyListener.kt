package dev.slne.surf.skill.core.paper.skills.alchemy.listeners

import dev.slne.surf.skill.api.paper.SkillInstance
import dev.slne.surf.skill.api.paper.player.incrementExperience
import dev.slne.surf.skill.api.paper.player.skillPlayer
import dev.slne.surf.skill.api.paper.skills.AlchemySkill
import dev.slne.surf.skill.core.paper.skills.utils.SkillLevelingHandler
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.AreaEffectCloudApplyEvent
import org.bukkit.event.entity.LingeringPotionSplashEvent
import org.bukkit.event.entity.PotionSplashEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.persistence.PersistentDataType

object AlchemyListener : Listener {
    private val ALCHEMY_RESULT_ITEM_KEY = NamespacedKey("surf", "skill_alchemy_result_item")

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBrew(event: InventoryClickEvent) {
        if (event.isCancelled) return
        if (event.clickedInventory?.type != InventoryType.BREWING) return

        val clickedSlot = event.slotType
        if (clickedSlot != InventoryType.SlotType.CRAFTING) return

        val clickedItem = event.currentItem ?: event.cursor
        val itemType = clickedItem.type

        if (itemType != Material.POTION && itemType != Material.SPLASH_POTION && itemType != Material.LINGERING_POTION) return

        if (clickedItem.persistentDataContainer.has(ALCHEMY_RESULT_ITEM_KEY)) return
        clickedItem.editPersistentDataContainer { pdc ->
            pdc.set(ALCHEMY_RESULT_ITEM_KEY, PersistentDataType.BOOLEAN, true)
        }

        val player = event.whoClicked as? Player ?: return

        if (!SkillLevelingHandler.canCollectExperience(player, AlchemySkill)) {
            return
        }

        SkillInstance.launch {
            val skillPlayer = player.skillPlayer()

            skillPlayer.incrementExperience<AlchemySkill>(5)
        }
    }

    private fun handlePotionHit(shooter: Player, affectedEntities: List<LivingEntity>) {
        val targetsHit = affectedEntities.filter { it != shooter }
        val playersHit = targetsHit.filterIsInstance<Player>()
        val entitiesHit = targetsHit - playersHit.toSet()

        val playerExperience = playersHit.size * 5
        val entityExperience = entitiesHit.size
        val totalExperience = playerExperience + entityExperience

        if (totalExperience <= 0) return

        SkillInstance.launch {
            val skillPlayer = shooter.skillPlayer()

            skillPlayer.incrementExperience<AlchemySkill>(totalExperience)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onLingeringHit(event: LingeringPotionSplashEvent) {
        if (event.isCancelled) return

        val shooter = event.entity.shooter as? Player ?: return

        if (!SkillLevelingHandler.canCollectExperience(shooter, AlchemySkill)) {
            return
        }

        SkillInstance.launch {
            val skillPlayer = shooter.skillPlayer()

            skillPlayer.incrementExperience<AlchemySkill>(1)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onSplash(event: PotionSplashEvent) {
        if (event.isCancelled) return

        val potion = event.entity
        val shooter = potion.shooter as? Player ?: return

        if (!SkillLevelingHandler.canCollectExperience(shooter, AlchemySkill)) {
            return
        }

        handlePotionHit(shooter, event.affectedEntities.toList())
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onEffect(event: AreaEffectCloudApplyEvent) {
        if (event.isCancelled) return

        val shooter = event.entity.source as? Player ?: return
        val affectedEntities = event.affectedEntities

        if (!SkillLevelingHandler.canCollectExperience(shooter, AlchemySkill)) {
            return
        }

        handlePotionHit(shooter, affectedEntities)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onDrink(event: PlayerItemConsumeEvent) {
        if (event.isCancelled) return

        val item = event.item
        val player = event.player

        if (item.type != Material.POTION) return


        if (!SkillLevelingHandler.canCollectExperience(player, AlchemySkill)) {
            return
        }

        SkillInstance.launch {
            val skillPlayer = player.skillPlayer()

            skillPlayer.incrementExperience<AlchemySkill>(5)
        }
    }
}