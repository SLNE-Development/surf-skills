@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.api.paper.level

import org.bukkit.inventory.ItemType

enum class LevelState(
    val itemType: ItemType
) {
    UNREACHED(ItemType.RED_STAINED_GLASS_PANE),
    REACHED(ItemType.GREEN_STAINED_GLASS_PANE),
    CURRENT(ItemType.YELLOW_STAINED_GLASS_PANE);
}