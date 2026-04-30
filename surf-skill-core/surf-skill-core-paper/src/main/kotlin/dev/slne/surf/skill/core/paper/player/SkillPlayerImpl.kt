package dev.slne.surf.skill.core.paper.player

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.playSound
import dev.slne.surf.api.core.util.freeze
import dev.slne.surf.api.core.util.mutableObjectListOf
import dev.slne.surf.api.paper.extensions.server
import dev.slne.surf.skill.api.paper.Skill
import dev.slne.surf.skill.api.paper.experience.SkillExperience
import dev.slne.surf.skill.api.paper.manager.SkillManager
import dev.slne.surf.skill.api.paper.player.SkillPlayer
import dev.slne.surf.skill.core.paper.experience.SkillExperienceImpl
import dev.slne.surf.skill.core.paper.settings.SettingsHook
import dev.slne.surf.skill.core.paper.settings.hasSettingsApi
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.sound.Sound
import java.util.*
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.seconds
import org.bukkit.Sound as BukkitSound

class SkillPlayerImpl(
    override val uuid: UUID,
    experiences: ObjectList<SkillExperience>
) : SkillPlayer {
    private val _experiences = mutableObjectListOf<SkillExperience>(experiences)
    override val experiences = _experiences.freeze()

    override val player get() = server.getPlayer(uuid)
    override val offlinePlayer get() = server.getOfflinePlayer(uuid)

    private val pickUpCache = Caffeine
        .newBuilder()
        .expireAfterWrite(3.seconds)
        .build<Pair<UUID, Skill>, Int>()

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
        if (amount < 1) {
            return
        }

        val experience = findOrCreateExperience(clazz).incrementExperience(amount)

        val player = player

        if (hasSettingsApi() && player != null && SettingsHook.hasGainXpSoundEnabled(player.uniqueId)) {
            player.playSound(true) {
                type(BukkitSound.ENTITY_EXPERIENCE_ORB_PICKUP)
                volume(.25f)
                pitch(1.25f)
                source(Sound.Source.AMBIENT)
            }
        }

        val cachedValue = pickUpCache.getIfPresent(uuid to experience.skill) ?: 0
        val newValue = cachedValue + amount
        pickUpCache.put(uuid to experience.skill, newValue)

        val totalXp = experience.currentExperience
        val curve = experience.skill.experienceCurve

        val currentLevel = curve.getLevelForExperience(totalXp)
        val xpForCurrentLevel = curve.getExperienceForLevel(currentLevel - 1)
        val xpForNextLevel = curve.getExperienceForLevel(currentLevel)

        val xpInLevel = totalXp - xpForCurrentLevel
        val xpNeeded = xpForNextLevel - xpForCurrentLevel

        player?.sendActionBar(buildText {
            spacer("»")
            appendSpace()
            append(experience.skill.displayName)
            appendSpace()
            spacer("‖")
            appendSpace()
            success("+$newValue XP")
            appendSpace()
            spacer("(")
            variableValue(xpInLevel)
            spacer("/")
            variableValue(xpNeeded)
            spacer(")")
            appendSpace()
            spacer("«")
        })
    }
}