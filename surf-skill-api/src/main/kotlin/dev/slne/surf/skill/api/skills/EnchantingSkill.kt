package dev.slne.surf.skill.api.skills

import dev.slne.surf.skill.api.Skill
import dev.slne.surf.api.core.util.requiredService

private val impl = requiredService<EnchantingSkill>()

interface EnchantingSkill : Skill {
    companion object : EnchantingSkill by impl
}