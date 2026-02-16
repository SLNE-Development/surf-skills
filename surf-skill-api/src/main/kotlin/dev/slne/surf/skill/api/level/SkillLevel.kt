package dev.slne.surf.skill.api.level

import dev.slne.surf.skill.api.Skill
import dev.slne.surf.skill.api.level.reward.LevelReward
import dev.slne.surf.skill.api.progress.SkillProgress
import dev.slne.surf.surfapi.bukkit.api.builder.LoreBuilder
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.jetbrains.annotations.Unmodifiable

interface SkillLevel {
    val skill: Skill
    val level: Int

    val description: (LoreBuilder.() -> Unit)?
    fun buildLore(progress: SkillProgress): ObjectList<Component>

    val rewards: @Unmodifiable ObjectList<LevelReward>

    suspend fun grantRewards(player: Player)
}