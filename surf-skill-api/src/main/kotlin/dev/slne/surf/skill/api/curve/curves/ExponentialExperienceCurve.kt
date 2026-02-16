package dev.slne.surf.skill.api.curve.curves

import dev.slne.surf.skill.api.curve.ExperienceCurve
import kotlin.math.pow
import kotlin.math.roundToInt

open class ExponentialExperienceCurve(
    private val maxLevel: Int,
    private val maxExperience: Int,
    private val baseExperience: Int
) : ExperienceCurve {
    private val experiencePerLevel = IntArray(maxLevel + 1)
    private val totalExperience = IntArray(maxLevel + 1)

    init {
        val growthRate = calculateGrowthRate(maxLevel, maxExperience, baseExperience)

        var accumulated = 0
        for (level in 1..maxLevel) {
            val experience = (baseExperience * growthRate.pow(level - 1)).roundToInt()

            experiencePerLevel[level] = experience
            accumulated += experience
            totalExperience[level] = accumulated
        }
    }

    override fun getExperienceForLevel(level: Int): Int {
        return experiencePerLevel.getOrElse(level) { experiencePerLevel[maxLevel] }
    }

    override fun getTotalExperienceForLevel(level: Int): Int {
        return totalExperience.getOrElse(level) { totalExperience[maxLevel] }
    }

    override fun getLevelForExperience(experience: Int): Int {
        for (level in 1..maxLevel) {
            if (experience < totalExperience[level]) return level
        }

        return maxLevel
    }

    private fun calculateGrowthRate(
        maxLevel: Int,
        maxExperience: Int,
        baseExperience: Int,
        precision: Double = 1e-6
    ): Double {
        var low = 1.0
        var high = 5.0
        var mid: Double

        while (high - low > precision) {
            mid = (low + high) / 2.0

            val total = baseExperience * ((mid.pow(maxLevel.toDouble()) - 1) / (mid - 1))

            if (total < maxExperience) low = mid else high = mid
        }

        return (low + high) / 2.0
    }
}