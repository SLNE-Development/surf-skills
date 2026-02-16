package dev.slne.surf.skill.paper.menu.utils

import dev.slne.surf.surfapi.bukkit.api.builder.ItemStack
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import me.devnatan.inventoryframework.View
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

val View.outlineItem: ItemStack
    get() = ItemStack(Material.GRAY_STAINED_GLASS_PANE) {
        displayName(text(""))
    }