package dev.slne.surf.skill.api.common.curve

interface ExperienceCurve {
    fun getExperienceForLevel(level: Int): Int
    fun getTotalExperienceForLevel(level: Int): Int
    fun getLevelForExperience(experience: Int): Int
}