package dev.slne.surf.skill.api.paper.skills

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.skill.api.paper.Skill

private val impl = requiredService<CombatSkill>()

interface CombatSkill : Skill {
    companion object : CombatSkill by impl
}