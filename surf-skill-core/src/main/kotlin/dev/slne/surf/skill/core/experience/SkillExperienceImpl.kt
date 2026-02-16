package dev.slne.surf.skill.core.experience

import dev.slne.surf.skill.api.Skill
import dev.slne.surf.skill.api.experience.SkillExperience
import dev.slne.surf.skill.api.level.LevelState
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
        currentExperience += amount

        return this
    }

    override fun decrementExperience(amount: Int): SkillExperience {
        currentExperience = (currentExperience - amount).coerceAtLeast(0)

        return this
    }
}
