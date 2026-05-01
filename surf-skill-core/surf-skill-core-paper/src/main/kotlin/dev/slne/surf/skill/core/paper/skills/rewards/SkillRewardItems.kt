@file:Suppress("UnstableApiUsage", "DEPRECATION")

package dev.slne.surf.skill.core.paper.skills.rewards

import dev.slne.surf.api.paper.builder.buildItem
import dev.slne.surf.api.paper.builder.lore
import dev.slne.surf.api.paper.builder.meta
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Color
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemRarity
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ItemType
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.inventory.meta.SuspiciousStewMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

private val SPECIAL_ITEM_KEY = NamespacedKey("surf-freebuild-paper", "special_item")

fun combatResistancePotion() = customPotion(
    name = "Resistance Potion",
    color = Color.fromRGB(10329495),
    effect = PotionEffect(PotionEffectType.RESISTANCE, 24000, 3, false, true, true)
)

fun hastePotion() = customPotion(
    name = "Haste Potion",
    color = Color.fromRGB(16701501),
    effect = PotionEffect(PotionEffectType.HASTE, 24000, 9, false, true, true)
)

fun dolphinsGracePotion() = customPotion(
    name = "Dolphin's Grace Potion",
    color = Color.fromRGB(3847130),
    effect = PotionEffect(PotionEffectType.DOLPHINS_GRACE, 24000, 3, false, true, true)
)

fun saturationStew() = buildItem(ItemType.SUSPICIOUS_STEW) {
    meta<SuspiciousStewMeta> {
        setRarity(ItemRarity.RARE)
        addCustomEffect(PotionEffect(PotionEffectType.SATURATION, 1, 0, false, true, true), true)
    }

    lore(
        Component.text("Saturation (02:00:00)", NamedTextColor.BLUE)
            .decoration(TextDecoration.ITALIC, false)
    )
}

fun totemOfUndying() = ItemType.TOTEM_OF_UNDYING.createItemStack()
fun netheriteIngot() = ItemType.NETHERITE_INGOT.createItemStack()
fun heartOfTheSea() = ItemType.HEART_OF_THE_SEA.createItemStack()
fun ironBlocks() = ItemType.IRON_BLOCK.createItemStack(16)

fun experienceBook() = specialStoredEnchantedBook(
    enchantment = enchantment("surf", "experience"),
    level = 4,
    hideEnchantments = true
)

fun soulboundBook() = storedEnchantedBook(
    enchantment = enchantment("surf", "soulbound"),
    level = 1
)

fun surfMendingBook() = storedEnchantedBook(
    enchantment = enchantment("surf", "mending"),
    level = 1
)

fun lootingBook() = specialStoredEnchantedBook(
    enchantment = Enchantment.LOOTING,
    level = 5
)

fun efficiencyBook(level: Int) = specialStoredEnchantedBook(
    enchantment = Enchantment.EFFICIENCY,
    level = level
)

fun featherFallingBook() = specialStoredEnchantedBook(
    enchantment = Enchantment.FEATHER_FALLING,
    level = 7
)

fun lureBook() = specialStoredEnchantedBook(
    enchantment = Enchantment.LURE,
    level = 4
)

fun riptideBook() = specialStoredEnchantedBook(
    enchantment = Enchantment.RIPTIDE,
    level = 6,
    hideEnchantments = true
)

fun combatBow() = buildItem(ItemType.BOW) {
    meta {
        setRarity(ItemRarity.EPIC)
        addEnchant(Enchantment.INFINITY, 1, true)
        addEnchant(Enchantment.MENDING, 1, true)
        addItemFlags(ItemFlag.HIDE_ENCHANTS)
        markSpecialItem()
    }
}

private fun customPotion(
    name: String,
    color: Color,
    effect: PotionEffect,
): ItemStack {
    return buildItem(ItemType.POTION) {
        meta<PotionMeta> {
            setRarity(ItemRarity.RARE)
            displayName(
                Component.text(name)
                    .decoration(TextDecoration.ITALIC, false)
            )
            setColor(color)
            addCustomEffect(effect, true)
        }
    }
}

private fun storedEnchantedBook(
    enchantment: Enchantment,
    level: Int,
    hideEnchantments: Boolean = false,
): ItemStack {
    return buildItem(ItemType.ENCHANTED_BOOK) {
        meta<EnchantmentStorageMeta> {
            addStoredEnchant(enchantment, level, true)

            if (hideEnchantments) {
                addItemFlags(ItemFlag.HIDE_ENCHANTS)
            }
        }
    }
}

private fun specialStoredEnchantedBook(
    enchantment: Enchantment,
    level: Int,
    hideEnchantments: Boolean = false,
): ItemStack {
    return storedEnchantedBook(enchantment, level, hideEnchantments).apply {
        meta {
            markSpecialItem()
        }
    }
}

private fun ItemMeta.markSpecialItem() {
    persistentDataContainer.set(SPECIAL_ITEM_KEY, PersistentDataType.BYTE, 1)
}

private fun enchantment(namespace: String, key: String): Enchantment {
    return requireNotNull(Registry.ENCHANTMENT.get(NamespacedKey(namespace, key))) {
        "Unknown enchantment $namespace:$key"
    }
}
