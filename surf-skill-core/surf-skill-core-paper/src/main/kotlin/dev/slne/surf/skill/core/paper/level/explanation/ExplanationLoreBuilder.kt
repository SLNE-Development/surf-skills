package dev.slne.surf.skill.core.paper.level.explanation

import dev.slne.surf.api.core.messages.builder.SurfComponentBuilder
import dev.slne.surf.api.core.messages.builder.colors.SpacerComponentBuilderColor.spacer
import dev.slne.surf.api.core.messages.builder.colors.SuccessComponentBuilderColor.success
import dev.slne.surf.api.paper.builder.LoreBuilder
import dev.slne.surf.skill.api.experience.SkillExperience

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
            SurfComponentBuilder.appendSpace()
            spacer("($percent%)")
        }
    }

    companion object {
        const val AMOUNT_OF_BARS = 50
    }
}