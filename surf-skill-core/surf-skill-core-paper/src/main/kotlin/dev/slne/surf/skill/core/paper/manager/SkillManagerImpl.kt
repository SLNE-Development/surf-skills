package dev.slne.surf.skill.core.paper.manager

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.util.freeze
import dev.slne.surf.api.core.util.mutableObjectSetOf
import dev.slne.surf.skill.api.Skill
import dev.slne.surf.skill.api.manager.SkillManager
import dev.slne.surf.skill.api.skills.*
import net.kyori.adventure.util.Services
import kotlin.reflect.KClass

@AutoService(SkillManager::class)
class SkillManagerImpl : SkillManager, Services.Fallback {
    private val _skills = mutableObjectSetOf<Skill>()
    override val skills get() = _skills.freeze()

    fun registerAllSkills() {
        registerSkill(AlchemySkill)
        registerSkill(CombatSkill)
        registerSkill(EnchantingSkill)
        registerSkill(FishingSkill)
        registerSkill(ExplorationSkill)
        registerSkill(ForagingSkill)
        registerSkill(MiningSkill)
        registerSkill(WoodcuttingSkill)
    }

    fun registerListeners() {
        skills.forEach { it.registerListeners() }
    }

    override fun registerSkill(skill: Skill): Boolean {
        return _skills.add(skill)
    }

    override fun unregisterSkill(skill: Skill): Boolean {
        return _skills.remove(skill)
    }

    override fun getSkillByName(name: String) =
        _skills.firstOrNull { it.name.equals(name, true) }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Skill> getSkill(skillClazz: KClass<out T>) =
        _skills.firstOrNull { skillClazz.isInstance(it) } as? T
}

val skillManagerImpl get() = SkillManager.INSTANCE as SkillManagerImpl