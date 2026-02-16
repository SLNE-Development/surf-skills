package dev.slne.surf.skill.api.experience.utils

import dev.slne.surf.skill.api.Skill
import dev.slne.surf.skill.api.SkillInstance
import dev.slne.surf.skill.api.experience.SkillExperience
import dev.slne.surf.skill.api.manager.SkillManager
import it.unimi.dsi.fastutil.objects.ObjectList
import java.util.*
import kotlin.reflect.KClass

fun <S : Skill> ObjectList<SkillExperience>.findOrCreateSkillExperience(
    skillClass: KClass<out S>,
    uuid: UUID
): SkillExperience {
    val skillExperience = this.firstOrNull { skillClass.isInstance(it.skill) }

    if (skillExperience != null) {
        return skillExperience
    }

    val skill = SkillManager.getSkill(skillClass)
        ?: error("Trying to access unregistered skill ${skillClass.simpleName}")

    val newSkillExperience = SkillInstance.createSkillExperience(
        uuid = uuid,
        skill = skill,
        currentExperience = 0,
    )

    this.add(newSkillExperience)

    return newSkillExperience
}

inline fun <reified S : Skill> ObjectList<SkillExperience>.findOrCreateSkillExperience(uuid: UUID) =
    findOrCreateSkillExperience(S::class, uuid)