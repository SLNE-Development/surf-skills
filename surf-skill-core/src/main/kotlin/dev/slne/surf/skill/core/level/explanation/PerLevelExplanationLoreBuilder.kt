package dev.slne.surf.skill.core.level.explanation

import dev.slne.surf.skill.api.experience.SkillExperience
import dev.slne.surf.api.paper.builder.LoreBuilder

object PerLevelExplanationLoreBuilder : ExplanationLoreBuilder {
    override fun LoreBuilder.buildExplanation(
        experience: SkillExperience,
        level: Int,
        amountOfBars: Int
    ) {
        val skill = experience.skill
        val currentLevel = experience.currentLevel

        val currentLevelXp = skill.experienceCurve.getExperienceForLevel(level)
        val totalXpForPreviousLevel =
            if (level > 1) skill.experienceCurve.getTotalExperienceForLevel(level - 1) else 0

        val xpIntoLevel = when {
            currentLevel > level -> currentLevelXp
            currentLevel == level -> (experience.currentExperience - totalXpForPreviousLevel).coerceAtLeast(
                0
            )

            else -> 0L
        }

        val percent =
            (xpIntoLevel.toDouble() / currentLevelXp.toDouble() * 100.0).coerceIn(0.0, 100.0)

        appendExperienceLine(xpIntoLevel.toInt(), currentLevelXp)

        appendProgressBar(
            (percent / 100.0 * amountOfBars).toInt().coerceIn(0, amountOfBars),
            percent.toInt(),
            amountOfBars
        )
    }
}