package dev.slne.surf.skill.core.level.explanation

import dev.slne.surf.skill.api.experience.SkillExperience
import dev.slne.surf.surfapi.bukkit.api.builder.LoreBuilder

interface ExplanationLoreBuilder {
    fun LoreBuilder.buildExplanation(
        experience: SkillExperience,
        level: Int,
        amountOfBars: Int
    )

    fun LoreBuilder.appendExperienceLine(
        current: Int,
        max: Int
    ) {
        line {
            spacer("$current / $max XP")
        }
    }

    fun LoreBuilder.appendProgressBar(
        barsFilled: Int,
        percent: Int,
        amountOfBars: Int
    ) {
        line {
            for (i in 1..amountOfBars) {
                when {
                    i <= barsFilled -> success("|")
                    else -> error("|")
                }
            }
            appendSpace()
            spacer("($percent%)")
        }
    }

    companion object {
        const val AMOUNT_OF_BARS = 50
    }
}