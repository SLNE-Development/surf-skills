package dev.slne.surf.skill.paper.utils

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object SkillPermissionRegistry: PermissionRegistry() {
    private const val PREFIX = "surf.skill"
    private const val COMMAND_PREFIX = "$PREFIX.command"

    val COMMAND_SKILL = create("$COMMAND_PREFIX.skill")
}