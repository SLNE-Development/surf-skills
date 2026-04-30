package dev.slne.surf.skill.core.paper.util

import dev.slne.surf.skill.api.paper.Skill
import org.bukkit.GameMode
import org.bukkit.entity.Player

object SkillLevelingHandler {
    fun canCollectExperience(player: Player?, skill: Skill): Boolean {
        if (player == null) {
            return false
        }

        if (!skill.active) {
            return false
        }

        if (player.gameMode != GameMode.SURVIVAL) {
            return false
        }

        if (!player.hasPermission("surf.skill.${skill.name.lowercase()}")) {
            return false
        }

        return true
    }
}