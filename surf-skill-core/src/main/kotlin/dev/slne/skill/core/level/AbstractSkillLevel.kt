package dev.slne.skill.core.level

import dev.slne.skill.core.level.explanation.ExplanationLoreBuilder
import dev.slne.skill.core.level.explanation.GlobalXPWithPerLevelBarLoreBuilder
import dev.slne.surf.skill.api.Skill
import dev.slne.surf.skill.api.experience.SkillExperience
import dev.slne.surf.skill.api.level.SkillLevel
import dev.slne.surf.skill.api.level.reward.LevelReward
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

    override fun buildLore(experience: SkillExperience): ObjectList<Component> {
        return LoreBuilder().apply {
            emptyLine()
            buildDescriptionLore()
            buildLevelExplanationLore(experience)
            buildRewardLore()
            emptyLine()
        }.build().toObjectList()
    }

    private fun LoreBuilder.buildLevelExplanationLore(experience: SkillExperience) {
        GlobalXPWithPerLevelBarLoreBuilder.run {
            buildExplanation(
                experience = experience,
                level = level,
                amountOfBars = ExplanationLoreBuilder.AMOUNT_OF_BARS
            )
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