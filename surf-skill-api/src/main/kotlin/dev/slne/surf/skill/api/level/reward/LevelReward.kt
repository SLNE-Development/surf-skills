package dev.slne.surf.skill.api.level.reward

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

interface LevelReward {
    val displayName: Component

    suspend fun grant(player: Player)
}