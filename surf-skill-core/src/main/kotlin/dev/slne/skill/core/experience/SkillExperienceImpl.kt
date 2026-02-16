package dev.slne.skill.core.experience

import dev.slne.surf.skill.api.Skill
import dev.slne.surf.skill.api.level.LevelState
import dev.slne.surf.skill.api.progress.SkillExperience

data class SkillExperienceImpl(
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
}
