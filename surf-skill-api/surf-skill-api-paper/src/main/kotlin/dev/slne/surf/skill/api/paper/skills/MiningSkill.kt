package dev.slne.surf.skill.api.paper.skills

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.skill.api.paper.Skill

private val impl = requiredService<MiningSkill>()

interface MiningSkill : Skill {
    companion object : MiningSkill by impl
}