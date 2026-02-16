package dev.slne.skill.core.level.explanation

import dev.slne.surf.skill.api.experience.SkillExperience
import dev.slne.surf.surfapi.bukkit.api.builder.LoreBuilder

object GlobalXPWithPerLevelBarLoreBuilder : ExplanationLoreBuilder {
    override fun LoreBuilder.buildExplanation(
        experience: SkillExperience,
        level: Int,
        amountOfBars: Int
    ) {
        val skill = experience.skill
        val currentLevel = experience.currentLevel

        val xpForPreviousLevels = if (level > 1) {
            skill.experienceCurve.getTotalExperienceForLevel(level - 1)
        } else {
            0
        }
        val currentLevelXp = skill.experienceCurve.getExperienceForLevel(level)

        val xpIntoLevel = when {
            currentLevel > level -> currentLevelXp
            currentLevel == level -> (experience.currentExperience - xpForPreviousLevels).coerceAtLeast(
                0
            )

            else -> 0
        }

        appendExperienceLine(xpIntoLevel, currentLevelXp)

        appendProgressBar(
            (xpIntoLevel.toDouble() / currentLevelXp.toDouble() * amountOfBars).toInt()
                .coerceIn(0, amountOfBars),
            (xpIntoLevel.toDouble() / currentLevelXp * 100).toInt(),
            amountOfBars
        )
    }
}