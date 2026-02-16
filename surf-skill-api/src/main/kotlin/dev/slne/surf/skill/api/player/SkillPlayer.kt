package dev.slne.surf.skill.api.player

import dev.slne.surf.skill.api.Skill
import dev.slne.surf.skill.api.experience.SkillExperience
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.jetbrains.annotations.Unmodifiable
import java.util.*
import kotlin.reflect.KClass

interface SkillPlayer {
    val uuid: UUID

    val player: Player?
    val offlinePlayer: OfflinePlayer

    val experiences: @Unmodifiable ObjectList<SkillExperience>

    fun <S : Skill> findExperience(clazz: KClass<out S>): SkillExperience?
    fun <S : Skill> findOrCreateExperience(clazz: KClass<out S>): SkillExperience

    fun <S : Skill> incrementExperience(clazz: KClass<out S>, amount: Int)
    fun <S : Skill> decrementExperience(clazz: KClass<out S>, amount: Int)
}

inline fun <reified S : Skill> SkillPlayer.findExperience(): SkillExperience? =
    findExperience(S::class)

inline fun <reified S : Skill> SkillPlayer.findOrCreateExperience(): SkillExperience =
    findOrCreateExperience(S::class)

inline fun <reified S : Skill> SkillPlayer.incrementExperience(amount: Int) =
    incrementExperience(S::class, amount)

inline fun <reified S : Skill> SkillPlayer.decrementExperience(amount: Int) =
    decrementExperience(S::class, amount)