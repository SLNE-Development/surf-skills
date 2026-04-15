package dev.slne.surf.skill.api.skills

import dev.slne.surf.skill.api.Skill
import dev.slne.surf.api.core.util.requiredService

private val impl = requiredService<ExplorationSkill>()

interface ExplorationSkill : Skill {
    companion object : ExplorationSkill by impl
}