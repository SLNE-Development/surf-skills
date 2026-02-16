package dev.slne.surf.skill.api.manager

import dev.slne.surf.skill.api.Skill
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.jetbrains.annotations.Unmodifiable
import kotlin.reflect.KClass

private val skillManager = requiredService<SkillManager>()

interface SkillManager {
    val skills: @Unmodifiable ObjectSet<Skill>

    fun registerSkill(skill: Skill): Boolean
    fun unregisterSkill(skill: Skill): Boolean

    fun getSkillByName(name: String): Skill?
    fun <T : Skill> getSkill(skillClazz: KClass<out T>): T?

    companion object : SkillManager by skillManager {
        val INSTANCE get() = skillManager
    }
}

inline fun <reified T : Skill> SkillManager.getSkill() = getSkill(T::class)