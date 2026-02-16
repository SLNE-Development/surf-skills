package dev.slne.surf.skill.api.progress

import dev.slne.surf.skill.api.Skill
import dev.slne.surf.skill.api.level.LevelState

interface SkillExperience {
    val skill: Skill

    val currentExperience: Int
    val currentLevel: Int

    fun checkLevel(level: Int): LevelState
}