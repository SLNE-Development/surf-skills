package dev.slne.surf.skill.core.ability

import dev.slne.surf.skill.api.Skill
import dev.slne.surf.skill.api.player.SkillPlayer
import dev.slne.surf.skill.api.player.SkillPlayerManager
import dev.slne.surf.skill.api.player.findExperience
import org.bukkit.entity.Player
import kotlin.random.Random
import kotlin.reflect.KClass

object AbilityUtil {
    /**
     * Calculates a linearly scaled ability value based on the player's current level.
     *
     * @param currentLevel the player's current level in the skill
     * @param minLevel the minimum level at which the ability becomes active
     * @param maxLevel the maximum skill level (default 50)
     * @param maxValue the maximum effect value at max level
     * @return the scaled effect value, or 0.0 if below minLevel
     */
    fun calculateScaledValue(
        currentLevel: Int,
        minLevel: Int,
        maxLevel: Int = Skill.MAX_SKILL_LEVEL,
        maxValue: Double
    ): Double {
        if (currentLevel < minLevel) return 0.0
        val effectiveLevel = currentLevel.coerceAtMost(maxLevel)
        return maxValue * (effectiveLevel - minLevel).toDouble() / (maxLevel - minLevel)
    }

    /**
     * Gets the player's current level for a given skill, using the synchronous cache.
     * Returns 0 if the player is not cached.
     */
    inline fun <reified S : Skill> getPlayerLevel(player: Player): Int {
        return getPlayerLevel(player, S::class)
    }

    fun <S : Skill> getPlayerLevel(player: Player, skillClass: KClass<out S>): Int {
        val skillPlayer = SkillPlayerManager.getPlayerIfCached(player.uniqueId) ?: return 0
        return skillPlayer.findExperience(skillClass)?.currentLevel ?: 0
    }

    /**
     * Performs a random check with the given probability.
     *
     * @param chance probability between 0.0 and 1.0
     * @return true if the check passes
     */
    fun rollChance(chance: Double): Boolean {
        if (chance <= 0.0) return false
        if (chance >= 1.0) return true
        return Random.nextDouble() < chance
    }
}
