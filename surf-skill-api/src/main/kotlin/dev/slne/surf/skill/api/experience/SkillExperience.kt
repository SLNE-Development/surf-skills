package dev.slne.surf.skill.api.experience

import dev.slne.surf.skill.api.Skill
import dev.slne.surf.skill.api.level.LevelState
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*

interface SkillExperience {
    val uuid: UUID
    val skill: Skill

    val currentExperience: Int
    val currentLevel: Int

    val player: Player?
    val offlinePlayer: OfflinePlayer

    fun checkLevel(level: Int): LevelState

    fun incrementExperience(amount: Int): SkillExperience
    fun decrementExperience(amount: Int): SkillExperience
}