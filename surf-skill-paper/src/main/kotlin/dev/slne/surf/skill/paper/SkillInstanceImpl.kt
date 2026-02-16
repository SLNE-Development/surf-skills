package dev.slne.surf.skill.paper

import com.github.shynixn.mccoroutine.folia.launch
import com.google.auto.service.AutoService
import dev.slne.skill.core.experience.SkillExperienceImpl
import dev.slne.surf.skill.api.Skill
import dev.slne.surf.skill.api.SkillInstance
import kotlinx.coroutines.CoroutineScope
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(SkillInstance::class)
class SkillInstanceImpl : SkillInstance, Services.Fallback {
    override fun launch(block: suspend CoroutineScope.() -> Unit) = plugin.launch(block = block)

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