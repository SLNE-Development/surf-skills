package dev.slne.skill.core.level

import dev.slne.surf.skill.api.Skill
import dev.slne.surf.skill.api.level.SkillLevel
import dev.slne.surf.skill.api.level.reward.LevelReward
import dev.slne.surf.skill.api.progress.SkillExperience
import dev.slne.surf.surfapi.bukkit.api.builder.LoreBuilder
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.util.freeze
import dev.slne.surf.surfapi.core.api.util.objectListOf
import dev.slne.surf.surfapi.core.api.util.toObjectList
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

abstract class AbstractSkillLevel(
    override val skill: Skill,
    override val level: Int,
    override val description: (LoreBuilder.() -> Unit)? = null,
) : SkillLevel {
    private val _rewards = buildRewards()
    override val rewards get() = _rewards.freeze()

    override fun buildLore(progress: SkillExperience): ObjectList<Component> {
        return LoreBuilder().apply {
            emptyLine()
            buildDescriptionLore()
            buildLevelExplanationLore(progress)
            emptyLine()
            buildRewardLore()
            emptyLine()
        }.build().toObjectList()
    }

    private fun LoreBuilder.buildLevelExplanationLore(progress: SkillExperience) {
        val skill = progress.skill
        val level = this@AbstractSkillLevel.level
        val currentLevel = progress.currentLevel

        val currentLevelXp = skill.experienceCurve.getExperienceForLevel(level)
        val totalXpForPreviousLevel =
            if (level > 1) skill.experienceCurve.getTotalExperienceForLevel(level - 1) else 0

        val xpIntoLevel = when {
            currentLevel > level -> currentLevelXp
            currentLevel == level -> (progress.currentExperience - totalXpForPreviousLevel).coerceAtLeast(
                0
            )

            else -> 0L
        }

        val percent =
            (xpIntoLevel.toDouble() / currentLevelXp.toDouble() * 100.0).coerceIn(0.0, 100.0)

        line {
            spacer("$xpIntoLevel / $currentLevelXp XP")
        }

        val totalBars = 50
        val barsFilled = (percent / 100.0 * totalBars).toInt().coerceIn(0, totalBars)

        line {
            for (i in 1..totalBars) {
                when {
                    i <= barsFilled -> success("|")
                    else -> error("|")
                }
            }
            appendSpace()
            spacer("(${percent.toInt()}%)")
        }
    }

    private fun LoreBuilder.buildDescriptionLore() {
        if (description != null) {
            description?.invoke(this)
            emptyLine()
        }
    }

    private fun LoreBuilder.buildRewardLore() {
        line {
            primary("Belohnungen:".toSmallCaps())
        }
        emptyLine()

        rewards.forEach { reward ->
            line {
                spacer("- ")
                append(reward.displayName)
            }
        }

        if (rewards.isEmpty()) {
            line {
                spacer("Keine Belohnungen".toSmallCaps())
            }
        }
    }

    open fun buildRewards(): ObjectList<LevelReward> {
        return objectListOf()
    }

    override suspend fun grantRewards(player: Player) {
        rewards.forEach { it.grant(player) }
    }
}