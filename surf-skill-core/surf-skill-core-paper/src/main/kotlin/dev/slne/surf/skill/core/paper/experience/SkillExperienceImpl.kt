package dev.slne.surf.skill.core.paper.experience

import dev.slne.surf.skill.api.experience.SkillExperience
import dev.slne.surf.skill.api.level.LevelState
import dev.slne.surf.skill.api.paper.Skill
import dev.slne.surf.skill.api.paper.SkillInstance
import java.util.*

data class SkillExperienceImpl(
    override val uuid: UUID,
    override val skill: Skill,
    override var currentExperience: Int,
) : SkillExperience {
    override val currentLevel: Int
        get() = skill.experienceCurve.getLevelForExperience(currentExperience)

    override fun checkLevel(level: Int): LevelState {
        val currentLevel = currentLevel

        return when {
            currentLevel < level -> LevelState.UNREACHED
            level == currentLevel -> LevelState.CURRENT
            else -> LevelState.REACHED
        }
    }

    override fun incrementExperience(amount: Int): SkillExperience {
        val beforeAdding = currentLevel
        currentExperience += amount
        val afterAdding = currentLevel

        if (beforeAdding != afterAdding) {
            SkillInstance.launch {
                skill.awardLevelUpRewards(uuid, beforeAdding)
            }
        }

        return this
    }
}
