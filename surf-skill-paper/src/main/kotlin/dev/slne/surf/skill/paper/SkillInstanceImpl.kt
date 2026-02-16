package dev.slne.surf.skill.paper

import com.github.shynixn.mccoroutine.folia.*
import com.google.auto.service.AutoService
import dev.slne.surf.skill.api.Skill
import dev.slne.surf.skill.api.SkillInstance
import dev.slne.surf.skill.core.experience.SkillExperienceImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import net.kyori.adventure.util.Services
import org.bukkit.Location
import org.bukkit.entity.Entity
import java.util.*
import kotlin.coroutines.CoroutineContext

@AutoService(SkillInstance::class)
class SkillInstanceImpl : SkillInstance, Services.Fallback {
    override fun launch(
        context: CoroutineContext,
        start: CoroutineStart,
        block: suspend CoroutineScope.() -> Unit
    ) = plugin.launch(context, start, block)

    override val globalRegionDispatcher = plugin.globalRegionDispatcher
    override val asyncDispatcher = plugin.asyncDispatcher
    override val mainDispatcher = plugin.mainDispatcher
    override fun entityDispatcher(entity: Entity) = plugin.entityDispatcher(entity)
    override fun regionDispatcher(location: Location) = plugin.regionDispatcher(location)

    override fun createSkillExperience(
        uuid: UUID,
        skill: Skill,
        currentExperience: Int
    ) = SkillExperienceImpl(
        uuid = uuid,
        skill = skill,
        currentExperience = currentExperience
    )
}