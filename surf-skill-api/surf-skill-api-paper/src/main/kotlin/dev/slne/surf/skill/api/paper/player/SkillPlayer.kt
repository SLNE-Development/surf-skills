package dev.slne.surf.skill.api.paper.player

import dev.slne.surf.skill.api.paper.Skill
import dev.slne.surf.skill.api.paper.experience.SkillExperience
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

    fun <S : Skill> hasLevel(clazz: KClass<out S>, level: Int): Boolean

    fun <S : Skill> findExperience(clazz: KClass<out S>): SkillExperience?
    fun <S : Skill> findOrCreateExperience(clazz: KClass<out S>): SkillExperience

    fun <S : Skill> incrementExperience(clazz: KClass<out S>, amount: Int)
}

suspend fun Player.skillPlayer() = SkillPlayerManager.fetchOrCreatePlayer(uniqueId)

inline fun <reified S : Skill> SkillPlayer.hasLevel(level: Int): Boolean =
    hasLevel(S::class, level)

inline fun <reified S : Skill> SkillPlayer.findExperience(): SkillExperience? =
    findExperience(S::class)

inline fun <reified S : Skill> SkillPlayer.findOrCreateExperience(): SkillExperience =
    findOrCreateExperience(S::class)

inline fun <reified S : Skill> SkillPlayer.incrementExperience(amount: Int) =
    incrementExperience(S::class, amount)