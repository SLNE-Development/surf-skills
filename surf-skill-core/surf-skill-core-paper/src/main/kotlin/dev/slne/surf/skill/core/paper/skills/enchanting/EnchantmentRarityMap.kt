package dev.slne.surf.skill.core.paper.skills.enchanting

import dev.slne.surf.api.core.rarity.Rarity


enum class EnchantmentRarityMap(
    private val rarity: Rarity,
    val experience: Int
) {
    COMMON(Rarity.COMMON, 1),
    UNCOMMON(Rarity.UNCOMMON, 1),
    RARE(Rarity.RARE, 2),
    EPIC(Rarity.EPIC, 3),
    LEGENDARY(Rarity.LEGENDARY, 4),
    MYTHIC(Rarity.MYTHIC, 5);

    companion object {
        private val map = entries.associateBy { it.rarity }

        fun getByEnchantmentRarity(rarity: Rarity) = map[rarity] ?: COMMON
    }
}