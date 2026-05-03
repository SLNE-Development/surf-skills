package dev.slne.surf.skill.api.paper.skills

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.skill.api.paper.Skill

private val impl = requiredService<WoodcuttingSkill>()

interface WoodcuttingSkill : Skill {
    companion object : WoodcuttingSkill by impl
}