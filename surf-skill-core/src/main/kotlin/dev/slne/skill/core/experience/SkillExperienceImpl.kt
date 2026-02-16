package dev.slne.skill.core.experience

import dev.slne.surf.skill.api.Skill
import dev.slne.surf.skill.api.experience.SkillExperience
import dev.slne.surf.skill.api.level.LevelState
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.playSound
import net.kyori.adventure.sound.Sound
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*
import org.bukkit.Sound as BukkitSound

data class SkillExperienceImpl(
    override val uuid: UUID,
    override val skill: Skill,
    override var currentExperience: Int,
) : SkillExperience {
    override val player: Player?
        get() = server.getPlayer(uuid)

    override val offlinePlayer: OfflinePlayer
        get() = server.getOfflinePlayer(uuid)

    override val currentLevel: Int
        get() = skill.experienceCurve.getLevelForExperience(currentExperience)

    override fun checkLevel(level: Int): LevelState {
        val currentLevel = currentLevel

        return when {
            currentLevel < level -> LevelState.UNREACHED
            level == currentLevel -> LevelState.CURRENT
            else -> LevelState.REACHED
        }
    }

    override fun incrementExperience(amount: Int): SkillExperience {
        currentExperience += amount

        val player = player ?: return this

        player.playSound(true) {
            type(BukkitSound.ENTITY_EXPERIENCE_ORB_PICKUP)
            volume(.25f)
            pitch(1.25f)
            source(Sound.Source.AMBIENT)
        }

        player.sendActionBar(buildText {
            append(skill.displayName)
            appendSpace()
            success("+$amount")
        })

        return this
    }

    override fun decrementExperience(amount: Int): SkillExperience {
        currentExperience = (currentExperience - amount).coerceAtLeast(0)

        val player = player ?: return this

        player.playSound(true) {
            type(BukkitSound.ENTITY_VILLAGER_HURT)
            volume(.25f)
            pitch(1.25f)
            source(Sound.Source.AMBIENT)
        }

        player.sendActionBar(buildText {
            append(skill.displayName)
            appendSpace()
            error("-$amount")
        })

        return this
    }
}
