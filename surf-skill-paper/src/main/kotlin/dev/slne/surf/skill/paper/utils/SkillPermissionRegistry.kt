package dev.slne.surf.skill.paper.utils

import dev.slne.surf.api.paper.permission.PermissionRegistry
import dev.slne.surf.skill.api.paper.manager.SkillManager

object SkillPermissionRegistry : PermissionRegistry() {
    private const val PREFIX = "surf.skill"
    private const val COMMAND_PREFIX = "$PREFIX.command"

    val COMMAND_SKILL = create("$COMMAND_PREFIX.skill")
    val COMMAND_SKILL_OTHER = create("$COMMAND_SKILL.other")
    val COMMAND_SKILL_STATS = create("$COMMAND_SKILL.stats")


    init {
        SkillManager.skills.forEach {
            create("$PREFIX.${it.name.lowercase()}")
        }
    }
}