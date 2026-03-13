package dev.slne.surf.skill.api.skills

import dev.slne.surf.skill.api.Skill
import dev.slne.surf.surfapi.core.api.util.requiredService

private val impl = requiredService<AlchemySkill>()

interface AlchemySkill : Skill {
    companion object : AlchemySkill by impl
}