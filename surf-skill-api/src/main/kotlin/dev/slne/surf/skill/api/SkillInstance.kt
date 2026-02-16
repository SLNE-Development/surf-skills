package dev.slne.surf.skill.api

import dev.slne.surf.skill.api.experience.SkillExperience
import dev.slne.surf.surfapi.core.api.util.requiredService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import org.bukkit.Location
import org.bukkit.entity.Entity
import java.util.*
import kotlin.coroutines.CoroutineContext

private val instance = requiredService<SkillInstance>()

interface SkillInstance {
    fun launch(
        context: CoroutineContext = mainDispatcher,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job

    val globalRegionDispatcher: CoroutineContext
    val mainDispatcher: CoroutineContext
    val asyncDispatcher: CoroutineContext
    fun entityDispatcher(entity: Entity): CoroutineContext
    fun regionDispatcher(location: Location): CoroutineContext

    fun createSkillExperience(
        uuid: UUID,
        skill: Skill,
        currentExperience: Int
    ): SkillExperience

    companion object : SkillInstance by instance {
        val INSTANCE get() = instance
    }
}