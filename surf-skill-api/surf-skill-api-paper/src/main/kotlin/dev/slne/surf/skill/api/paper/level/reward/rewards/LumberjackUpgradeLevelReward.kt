package dev.slne.surf.skill.api.paper.level.reward.rewards

import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.paper.util.namespacedKey
import dev.slne.surf.skill.api.paper.level.reward.AbstractLevelReward
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

private val reductionKey = namespacedKey("lumberjack_cooldown_reduction")

class LumberjackUpgradeLevelReward(val reduction: Double) : AbstractLevelReward(
    displayName = buildText {
        primary("Lumberjack Upgrade:".toSmallCaps())
    },
    description = {
        line {
            info("Verringert den Cooldown des Lumberjack Enchantments um $reduction Sekunden")
        }
    }
) {
    override suspend fun grant(player: Player) {
        player.persistentDataContainer.set(reductionKey, PersistentDataType.DOUBLE, reduction)
    }
}