@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.paper.skills.mining

import com.destroystokyo.paper.MaterialTags
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player

object MiningRewardRules {
    /**
     * Decides whether breaking a block of [type] grants mining rewards (experience and abilities).
     *
     * - Ancient debris always drops itself, so silk touch is irrelevant and only the placed-block check applies.
     * - Other ores never grant rewards when mined with silk touch. Without silk touch they always do,
     *   since a placed ore can only come from an earlier silk touch break that granted nothing.
     * - All other blocks only grant rewards if they were not placed by a player.
     */
    inline fun isRewardable(player: Player, type: Material, eligible: () -> Boolean): Boolean {
        if (type == Material.ANCIENT_DEBRIS) {
            return eligible()
        }

        if (MaterialTags.ORES.isTagged(type)) {
            return !player.inventory.itemInMainHand.containsEnchantment(Enchantment.SILK_TOUCH)
        }

        return eligible()
    }
}
