package dev.slne.surf.skill.api.level.reward

import dev.slne.surf.api.paper.builder.LoreBuilder
import net.kyori.adventure.text.Component

abstract class AbstractLevelReward(
    override val displayName: Component,
    override val description: LoreBuilder.() -> Unit
) : LevelReward