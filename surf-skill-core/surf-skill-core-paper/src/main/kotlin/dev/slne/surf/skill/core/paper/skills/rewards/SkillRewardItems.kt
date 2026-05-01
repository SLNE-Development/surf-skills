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
import org.bukkit.inventory.ItemType
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.inventory.meta.SuspiciousStewMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

private val SPECIAL_ITEM_KEY = NamespacedKey("surf-freebuild-paper", "special_item")

fun rewardItem(type: ItemType, amount: Int = 1) = type.createItemStack(amount)

fun potionReward(
    name: String,
    color: Int,
    effectType: PotionEffectType,
    amplifier: Int,
    duration: Int,
) = buildItem(ItemType.POTION) {
    meta<PotionMeta> {
        setRarity(ItemRarity.RARE)
        displayName(
            Component.text(name)
                .decoration(TextDecoration.ITALIC, false)
        )
        setColor(Color.fromRGB(color))
        addCustomEffect(PotionEffect(effectType, duration, amplifier, false, true, true), true)
    }
}

fun suspiciousStewReward(
    loreText: String,
    effectType: PotionEffectType,
    duration: Int,
    amplifier: Int = 0,
) = buildItem(ItemType.SUSPICIOUS_STEW) {
    meta<SuspiciousStewMeta> {
        setRarity(ItemRarity.RARE)
        addCustomEffect(PotionEffect(effectType, duration, amplifier, false, true, true), true)
    }

    lore(
        Component.text(loreText, NamedTextColor.BLUE)
            .decoration(TextDecoration.ITALIC, false)
    )
}

fun enchantedBook(
    enchantment: Enchantment,
    level: Int,
    special: Boolean = false,
    hideEnchantments: Boolean = false,
) = buildItem(ItemType.ENCHANTED_BOOK) {
    meta<EnchantmentStorageMeta> {
        addStoredEnchant(enchantment, level, true)

        if (special) {
            markSpecialItem()
        }

        if (hideEnchantments) {
            addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }
    }
}

fun enchantedItem(
    type: ItemType,
    vararg enchantments: Pair<Enchantment, Int>,
    rarity: ItemRarity? = null,
    special: Boolean = false,
    hideEnchantments: Boolean = false,
) = buildItem(type) {
    meta {
        rarity?.let(::setRarity)
        enchantments.forEach { (enchantment, level) ->
            addEnchant(enchantment, level, true)
        }

        if (special) {
            markSpecialItem()
        }

        if (hideEnchantments) {
            addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }
    }
}

fun surfEnchantment(key: String) = enchantment("surf", key)
private fun ItemMeta.markSpecialItem() =
    persistentDataContainer.set(SPECIAL_ITEM_KEY, PersistentDataType.BYTE, 1)

@Suppress("SameParameterValue")
private fun enchantment(namespace: String, key: String) =
    requireNotNull(Registry.ENCHANTMENT.get(NamespacedKey(namespace, key))) {
        "Unknown enchantment $namespace:$key"
    }