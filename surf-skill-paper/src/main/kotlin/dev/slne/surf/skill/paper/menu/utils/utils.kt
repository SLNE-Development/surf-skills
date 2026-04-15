package dev.slne.surf.skill.paper.menu.utils

import dev.slne.surf.api.paper.builder.ItemStack
import dev.slne.surf.api.paper.builder.displayName
import dev.slne.surf.api.core.messages.adventure.text
import me.devnatan.inventoryframework.View
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

val View.outlineItem: ItemStack
    get() = ItemStack(Material.GRAY_STAINED_GLASS_PANE) {
        displayName(text(""))
    }