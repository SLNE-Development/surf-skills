package dev.slne.surf.skill.core

import dev.slne.surf.skill.api.Skill
import dev.slne.surf.skill.api.SkillInstance
import dev.slne.surf.skill.api.curve.curves.ExponentialExperienceCurve
import dev.slne.surf.skill.api.experience.SkillExperience
import dev.slne.surf.skill.api.level.SkillLevel
import dev.slne.surf.skill.core.level.EmptySkillLevel
import dev.slne.surf.surfapi.bukkit.api.builder.LoreBuilder
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.event.register
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.core.api.messages.adventure.playSound
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import dev.slne.surf.surfapi.core.api.util.objectListOf
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import it.unimi.dsi.fastutil.objects.ObjectList
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.withContext
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemType
import java.util.*
import org.bukkit.Sound as BukkitSound

abstract class AbstractSkill(
    override val name: String,
    override val material: ItemType,
    override val displayName: Component,
    override val lore: LoreBuilder.() -> Unit,
    override val baseExperience: Int = Skill.BASE_EXPERIENCE,
    override val maxLevel: Int = Skill.MAX_SKILL_LEVEL,
    override val maxExperience: Int = Skill.MAX_EXPERIENCE,
    listeners: ObjectSet<Listener> = objectSetOf()
) : Skill {
    private val _listeners = mutableObjectSetOf<Listener>(listeners)

    override val experienceCurve = ExponentialExperienceCurve(
        maxLevel = maxLevel,
        maxExperience = maxExperience,
        baseExperience = baseExperience
    )

    open fun getExtraLevels(): ObjectList<SkillLevel> {
        return objectListOf()
    }

    override suspend fun awardLevelUpRewards(uuid: UUID, level: Int) {
        val levelRewards = getLevels().firstOrNull { it.level == level }?.rewards ?: objectListOf()
        if (levelRewards.isEmpty()) return

        val player = server.getPlayer(uuid) ?: return

        withContext(SkillInstance.entityDispatcher(player)) {
            levelRewards.forEach { it.grant(player) }

            player.sendText {
                appendInfoPrefix()
                info("Du hast Level ")
                variableValue(level)
                info(" in ")
                append(displayName)
                info(" erreicht und folgende Belohnungen erhalten:")

                // TODO: Show rewards in message
            }

            player.playSound(true) {
                type(BukkitSound.ENTITY_PLAYER_LEVELUP)
                volume(.5f)
                source(Sound.Source.AMBIENT)
                pitch(.25f)
            }
        }
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
        _listeners.forEach { it.register() }
    }

    override fun asComponent() = displayName
}