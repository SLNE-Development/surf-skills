package dev.slne.surf.skill.api.skills

import dev.slne.surf.skill.api.Skill
import dev.slne.surf.surfapi.core.api.util.requiredService

private val impl = requiredService<CombatSkill>()

interface CombatSkill : Skill {
    companion object : CombatSkill by impl
}