package dev.slne.surf.skill.core.paper.util

import dev.slne.surf.skill.api.paper.Skill
import org.bukkit.GameMode
import org.bukkit.entity.Player

object SkillLevelingHandler {
    fun canCollectExperience(player: Player?, skill: Skill): Boolean {
        println("Checking if player can collect experience for skill ${skill.name}...")
        if (player == null) {
            return false
        }

        println("Checking if player ${player.name} can collect experience for skill ${skill.name}...")

        if (!skill.active) {
            return false
        }

        println("Player ${player.name} is active for skill ${skill.name}.")

        if (player.gameMode != GameMode.SURVIVAL) {
            return false
        }

        println("Player ${player.name} is in survival mode for skill ${skill.name}.")

        if (!player.hasPermission("surf.skill.${skill.name.lowercase()}")) {
            return false
        }

        println("Player ${player.name} has permission for skill ${skill.name}.")

        return true
    }
}