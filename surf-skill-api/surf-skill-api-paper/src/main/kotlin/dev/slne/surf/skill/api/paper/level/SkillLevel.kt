package dev.slne.surf.skill.api.paper.level

import dev.slne.surf.api.paper.builder.LoreBuilder
import dev.slne.surf.skill.api.paper.Skill
import dev.slne.surf.skill.api.paper.experience.SkillExperience
import dev.slne.surf.skill.api.paper.level.reward.LevelReward
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.jetbrains.annotations.Unmodifiable

interface SkillLevel {
    val skill: Skill
    val level: Int

    val description: (LoreBuilder.() -> Unit)?
    fun buildLore(experience: SkillExperience): ObjectList<Component>

    val rewards: @Unmodifiable ObjectList<LevelReward>

    suspend fun grantRewards(player: Player)
}