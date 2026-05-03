package dev.slne.surf.skill.api.paper.skills

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.skill.api.paper.Skill

private val impl = requiredService<ExplorationSkill>()

interface ExplorationSkill : Skill {
    companion object : ExplorationSkill by impl
}