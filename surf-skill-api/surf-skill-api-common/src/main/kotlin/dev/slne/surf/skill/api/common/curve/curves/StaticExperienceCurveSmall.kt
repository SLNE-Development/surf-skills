package dev.slne.surf.skill.api.common.curve.curves

import dev.slne.surf.skill.api.common.curve.ExperienceCurve

open class StaticExperienceCurveSmall : ExperienceCurve {

    private val experiencePerLevel = intArrayOf(
        0,
        5, 10, 15, 20, 25, 35, 50, 75, 100, 150,
        200, 250, 300, 400, 500, 600, 1000, 1500, 2000,
        4000, 6000, 8000, 10000, 12000, 14000, 16000, 18000,
        20000, 22000, 24000, 26000, 28000, 30000,
        32000, 34000, 36000, 38000, 40000,
        42000, 44000, 46000, 48000, 50000,
        52000, 55000, 58000, 62000, 68000,
        80000, 100000
    )

    private val totalExperience = IntArray(experiencePerLevel.size)

    init {
        var sum = 0
        for (i in 1 until experiencePerLevel.size) {
            sum += experiencePerLevel[i]
            totalExperience[i] = sum
        }
    }

    override fun getExperienceForLevel(level: Int): Int {
        return experiencePerLevel.getOrElse(level) { experiencePerLevel.last() }
    }

    override fun getTotalExperienceForLevel(level: Int): Int {
        return totalExperience.getOrElse(level) { totalExperience.last() }
    }

    override fun getLevelForExperience(experience: Int): Int {
        for (level in 1 until totalExperience.size) {
            if (experience < totalExperience[level]) return level
        }
        return experiencePerLevel.size
    }
}