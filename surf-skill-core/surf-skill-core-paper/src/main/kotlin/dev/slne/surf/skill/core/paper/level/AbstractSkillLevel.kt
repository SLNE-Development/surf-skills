package dev.slne.surf.skill.core.paper.level

import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.util.freeze
import dev.slne.surf.api.core.util.mutableObjectListOf
import dev.slne.surf.api.core.util.objectListOf
import dev.slne.surf.api.core.util.toObjectList
import dev.slne.surf.api.paper.builder.LoreBuilder
import dev.slne.surf.skill.api.paper.Skill
import dev.slne.surf.skill.api.paper.experience.SkillExperience
import dev.slne.surf.skill.api.paper.level.SkillLevel
import dev.slne.surf.skill.api.paper.level.reward.LevelReward
import dev.slne.surf.skill.core.paper.AbstractSkill
import dev.slne.surf.skill.core.paper.level.explanation.ExplanationLoreBuilder
import dev.slne.surf.skill.core.paper.level.explanation.PerLevelExplanationLoreBuilder
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

fun skillLevel(
    skill: Skill,
    level: Int,
    rewards: ObjectList<LevelReward>.() -> Unit = {},
    description: (LoreBuilder.() -> Unit)? = null,
) = object : AbstractSkillLevel(
    skill = skill,
    level = level,
    description = description,
) {
    override fun buildRewards(): ObjectList<LevelReward> {
        return mutableObjectListOf<LevelReward>().apply(rewards)
    }
}

open class AbstractSkillLevel(
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
            buildAbilityLore()
        }.build().toObjectList()
    }

    private fun LoreBuilder.buildLevelExplanationLore(experience: SkillExperience) {
        PerLevelExplanationLoreBuilder.run {
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
        if (rewards.isEmpty()) {
            return
        }

        line {
            primary("Belohnungen:".toSmallCaps())
        }
        emptyLine()

        rewards.forEach { reward ->
            line {
                spacer("- ")
                append(reward.displayName)
            }

            reward.description(this)
        }
    }

    private fun LoreBuilder.buildAbilityLore() {
        val skill = skill as? AbstractSkill ?: return
        val activeAbilities = skill.abilities.filter { it.isActiveAtLevel(level) }

        if (activeAbilities.isEmpty()) {
            return
        }

        line {
            primary("Fähigkeiten:".toSmallCaps())
        }
        emptyLine()

        activeAbilities.forEach { ability ->
            val newValue = ability.getFormattedValue(level)
            val isNew = !ability.isActiveAtLevel(level - 1)

            line {
                spacer("- ")
                append(ability.displayName)
                info(": ")

                if (isNew) {
                    variableValue(newValue)
                    spacer(" (")
                    variableValue("NEU!")
                    spacer(")")
                } else {
                    val oldValue = ability.getFormattedValue(level - 1)
                    spacer(oldValue)
                    info(" → ")
                    variableValue(newValue)
                }
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