package dev.slne.surf.skill.paper

import com.github.shynixn.mccoroutine.folia.*
import com.google.auto.service.AutoService
import dev.slne.surf.skill.api.paper.Skill
import dev.slne.surf.skill.api.paper.SkillInstance
import dev.slne.surf.skill.core.paper.PaperLoader
import dev.slne.surf.skill.core.paper.PaperSkillInstance
import dev.slne.surf.skill.core.paper.experience.SkillExperienceImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import net.kyori.adventure.util.Services
import org.bukkit.Location
import org.bukkit.entity.Entity
import java.util.*
import kotlin.coroutines.CoroutineContext

@AutoService(SkillInstance::class)
class SkillInstanceImpl : PaperSkillInstance, Services.Fallback {
    override fun launch(
        context: CoroutineContext,
        start: CoroutineStart,
        block: suspend CoroutineScope.() -> Unit
    ) = plugin.launch(context, start, block)

    override val globalRegionDispatcher by lazy {
        plugin.globalRegionDispatcher
    }
    override val asyncDispatcher by lazy {
        plugin.asyncDispatcher
    }
    override val mainDispatcher by lazy {
        plugin.mainDispatcher
    }

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

    override val paperLoader = PaperLoader(plugin.dataPath)
}