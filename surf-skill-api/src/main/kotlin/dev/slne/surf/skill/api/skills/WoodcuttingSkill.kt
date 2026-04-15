package dev.slne.surf.skill.api.skills

import dev.slne.surf.skill.api.Skill
import dev.slne.surf.api.core.util.requiredService

private val impl = requiredService<WoodcuttingSkill>()

interface WoodcuttingSkill : Skill {
    companion object : WoodcuttingSkill by impl
}