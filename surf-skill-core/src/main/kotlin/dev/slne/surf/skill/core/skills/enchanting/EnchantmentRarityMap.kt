package dev.slne.surf.skill.core.skills.enchanting

import dev.slne.surf.enchantment.api.utils.EnchantmentRarity

enum class EnchantmentRarityMap(
    private val rarity: EnchantmentRarity,
    val experience: Int
) {
    COMMON(EnchantmentRarity.COMMON, 1),
    UNCOMMON(EnchantmentRarity.UNCOMMON, 1),
    RARE(EnchantmentRarity.RARE, 2),
    EPIC(EnchantmentRarity.EPIC, 3),
    LEGENDARY(EnchantmentRarity.LEGENDARY, 4),
    MYTHIC(EnchantmentRarity.MYTHIC, 5);

    companion object {
        private val map = entries.associateBy { it.rarity }

        fun getByEnchantmentRarity(rarity: EnchantmentRarity) = map[rarity] ?: COMMON
    }
}