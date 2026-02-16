package dev.slne.skill.core.progress

import dev.slne.surf.skill.api.Skill
import dev.slne.surf.skill.api.level.LevelState
import dev.slne.surf.skill.api.progress.SkillProgress

data class SkillProgressImpl(
    override val skill: Skill,
    override val currentExperience: Int,
) : SkillProgress {
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
