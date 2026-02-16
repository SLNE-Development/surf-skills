package dev.slne.surf.skill.api

import dev.slne.surf.skill.api.experience.SkillExperience
import dev.slne.surf.surfapi.core.api.util.requiredService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.util.*

private val instance = requiredService<SkillInstance>()

interface SkillInstance {
    fun launch(block: suspend CoroutineScope.() -> Unit): Job

    fun createSkillExperience(
        uuid: UUID,
        skill: Skill,
        currentExperience: Int
    ): SkillExperience

    companion object : SkillInstance by instance {
        val INSTANCE get() = instance
    }
}