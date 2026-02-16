package dev.slne.surf.skill.api.level.reward

import dev.slne.surf.surfapi.bukkit.api.builder.LoreBuilder
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

interface LevelReward {
    val displayName: Component
    val description: LoreBuilder.() -> Unit

    suspend fun grant(player: Player)
}