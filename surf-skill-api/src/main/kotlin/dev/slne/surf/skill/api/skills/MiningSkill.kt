package dev.slne.surf.skill.api.skills

import dev.slne.surf.skill.api.Skill
import dev.slne.surf.surfapi.core.api.util.requiredService

private val impl = requiredService<MiningSkill>()

interface MiningSkill : Skill {
    companion object : MiningSkill by impl
}