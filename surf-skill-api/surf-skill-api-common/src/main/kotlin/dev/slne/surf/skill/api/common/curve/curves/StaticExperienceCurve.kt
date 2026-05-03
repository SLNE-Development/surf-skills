package dev.slne.surf.skill.api.common.curve.curves

import dev.slne.surf.skill.api.common.curve.ExperienceCurve

open class StaticExperienceCurve : ExperienceCurve {

    private val experiencePerLevel = intArrayOf(
        0,
        50, 125, 200, 300, 500, 750, 1000, 1500, 2000, 3500,
        5000, 7500, 10000, 15000, 20000, 30000, 50000, 75000, 100000,
        200000, 300000, 400000, 500000, 600000, 700000, 800000, 900000,
        1000000, 1100000, 1200000, 1300000, 1400000, 1500000,
        1600000, 1700000, 1800000, 1900000, 2000000,
        2100000, 2200000, 2300000, 2400000, 2500000,
        2600000, 2750000, 2900000, 3100000, 3400000,
        3700000, 4000000
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