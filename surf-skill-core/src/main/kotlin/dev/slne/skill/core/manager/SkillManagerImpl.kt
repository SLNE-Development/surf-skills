package dev.slne.skill.core.manager

import com.google.auto.service.AutoService
import dev.slne.skill.core.AbstractSkill
import dev.slne.skill.core.skills.alchemy.AlchemySkillImpl
import dev.slne.skill.core.skills.combat.CombatSkillImpl
import dev.slne.skill.core.skills.enchanting.EnchantingSkillImpl
import dev.slne.skill.core.skills.exploration.ExplorationSkillImpl
import dev.slne.skill.core.skills.fishing.FishingSkillImpl
import dev.slne.skill.core.skills.foraging.ForagingSkillImpl
import dev.slne.skill.core.skills.mining.MiningSkillImpl
import dev.slne.skill.core.skills.woodcutting.WoodcuttingSkillImpl
import dev.slne.surf.skill.api.Skill
import dev.slne.surf.skill.api.manager.SkillManager
import dev.slne.surf.surfapi.core.api.util.freeze
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import net.kyori.adventure.util.Services
import kotlin.reflect.KClass

@AutoService(SkillManager::class)
class SkillManagerImpl : SkillManager, Services.Fallback {
    private val _skills = mutableObjectSetOf<Skill>()
    override val skills get() = _skills.freeze()

    fun registerAllSkills() {
        registerSkill(AlchemySkillImpl)
        registerSkill(CombatSkillImpl)
        registerSkill(EnchantingSkillImpl)
        registerSkill(FishingSkillImpl)
        registerSkill(ExplorationSkillImpl)
        registerSkill(ForagingSkillImpl)
        registerSkill(MiningSkillImpl)
        registerSkill(WoodcuttingSkillImpl)
    }

    fun registerListeners() {
        skills.forEach { (it as? AbstractSkill)?.registerListeners() }
    }

    fun unregisterListeners() {
        skills.forEach { (it as? AbstractSkill)?.unregisterListeners() }
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