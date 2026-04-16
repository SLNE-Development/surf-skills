package dev.slne.surf.skill.core.paper.player

import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.playSound
import dev.slne.surf.api.core.util.freeze
import dev.slne.surf.api.core.util.mutableObjectListOf
import dev.slne.surf.api.paper.extensions.server
import dev.slne.surf.skill.api.Skill
import dev.slne.surf.skill.api.experience.SkillExperience
import dev.slne.surf.skill.api.manager.SkillManager
import dev.slne.surf.skill.api.player.SkillPlayer
import dev.slne.surf.skill.core.paper.experience.SkillExperienceImpl
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.sound.Sound
import java.util.*
import kotlin.reflect.KClass
import org.bukkit.Sound as BukkitSound

class SkillPlayerImpl(
    override val uuid: UUID,
    experiences: ObjectList<SkillExperience>
) : SkillPlayer {
    private val _experiences = mutableObjectListOf<SkillExperience>(experiences)
    override val experiences = _experiences.freeze()

    override val player get() = server.getPlayer(uuid)
    override val offlinePlayer get() = server.getOfflinePlayer(uuid)

    override fun <S : Skill> hasLevel(clazz: KClass<out S>, level: Int): Boolean =
        (findExperience(clazz)?.currentLevel ?: 0) >= level

    override fun <S : Skill> findExperience(clazz: KClass<out S>): SkillExperience? =
        experiences.firstOrNull { clazz.isInstance(it.skill) }

    override fun <S : Skill> findOrCreateExperience(clazz: KClass<out S>): SkillExperience {
        val old = findExperience(clazz)
        if (old != null) return old

        val skill = SkillManager.getSkill(clazz)
            ?: error("Trying to access unregistered skill ${clazz.simpleName ?: "Unknown Skill Class"}")

        val new = SkillExperienceImpl(
            uuid = uuid,
            skill = skill,
            currentExperience = 0
        )

        _experiences.add(new)

        return new
    }

    override fun <S : Skill> incrementExperience(clazz: KClass<out S>, amount: Int) {
        val experience = findOrCreateExperience(clazz).incrementExperience(amount)

        player?.playSound(true) {
            type(BukkitSound.ENTITY_EXPERIENCE_ORB_PICKUP)
            volume(.25f)
            pitch(1.25f)
            source(Sound.Source.AMBIENT)
        }

        player?.sendActionBar(buildText {
            append(experience.skill.displayName)
            appendSpace()
            success("+$amount XP")
        })
    }
}