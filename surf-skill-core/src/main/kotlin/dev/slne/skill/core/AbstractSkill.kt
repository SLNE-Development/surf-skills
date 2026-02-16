package dev.slne.skill.core

import dev.slne.skill.core.level.EmptySkillLevel
import dev.slne.surf.skill.api.Skill
import dev.slne.surf.skill.api.curve.curves.ExponentialExperienceCurve
import dev.slne.surf.skill.api.experience.SkillExperience
import dev.slne.surf.skill.api.level.SkillLevel
import dev.slne.surf.surfapi.bukkit.api.builder.LoreBuilder
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.event.register
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.objectListOf
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.text.Component
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemType

abstract class AbstractSkill(
    override val name: String,
    override val material: ItemType,
    override val displayName: Component,
    override val lore: LoreBuilder.() -> Unit,
    override val baseExperience: Int = Skill.BASE_EXPERIENCE,
    override val maxLevel: Int = Skill.MAX_SKILL_LEVEL,
    override val maxExperience: Int = Skill.MAX_EXPERIENCE
) : Skill {
    private val _listeners = mutableObjectListOf<Listener>()

    override val experienceCurve = ExponentialExperienceCurve(
        maxLevel = maxLevel,
        maxExperience = maxExperience,
        baseExperience = baseExperience
    )

    open fun getExtraLevels(): ObjectList<SkillLevel> {
        return objectListOf()
    }

    override fun getLevels(): ObjectList<SkillLevel> {
        val levels = mutableObjectListOf<SkillLevel>()
        val extra = getExtraLevels()

        for (level in 1..maxLevel) {
            val skillLevel = extra.firstOrNull { it.level == level } ?: EmptySkillLevel(
                skill = this,
                level = level
            )

            levels.add(skillLevel)
        }

        return levels
    }

    override fun displayItemStack(progress: SkillExperience) = buildItem(material) {
        displayName(displayName)

        val experience = progress.currentExperience
        val currentLevel = experienceCurve.getLevelForExperience(experience)

        buildLore {
            this@AbstractSkill.lore(this)
            emptyLine()

            line {
                variableKey("Level: ")
                variableValue(currentLevel)
            }
        }
    }

    fun registerListeners() {
        _listeners.addAll(registerSkillListeners())
        _listeners.forEach { it.register() }
    }

    open fun registerSkillListeners(): ObjectList<Listener> = mutableObjectListOf()

    override fun asComponent() = displayName
}