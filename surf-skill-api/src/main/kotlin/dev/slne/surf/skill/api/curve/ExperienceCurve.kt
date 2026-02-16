package dev.slne.surf.skill.api.curve

interface ExperienceCurve {
    fun getExperienceForLevel(level: Int): Int
    fun getTotalExperienceForLevel(level: Int): Int
    fun getLevelForExperience(experience: Int): Int
}