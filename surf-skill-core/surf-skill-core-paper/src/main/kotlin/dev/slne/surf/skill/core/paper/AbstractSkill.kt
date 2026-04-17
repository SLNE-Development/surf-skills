package dev.slne.surf.skill.core.paper

import dev.slne.surf.api.core.messages.adventure.playSound
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.util.freeze
import dev.slne.surf.api.core.util.mutableObjectListOf
import dev.slne.surf.api.core.util.objectListOf
import dev.slne.surf.api.paper.builder.LoreBuilder
import dev.slne.surf.api.paper.builder.buildItem
import dev.slne.surf.api.paper.builder.buildLore
import dev.slne.surf.api.paper.builder.displayName
import dev.slne.surf.api.paper.event.register
import dev.slne.surf.api.paper.extensions.server
import dev.slne.surf.api.paper.util.BukkitSound
import dev.slne.surf.skill.api.common.InternalSkillApi
import dev.slne.surf.skill.api.common.curve.curves.ExponentialExperienceCurve
import dev.slne.surf.skill.api.paper.Skill
import dev.slne.surf.skill.api.paper.SkillInstance
import dev.slne.surf.skill.api.paper.experience.SkillExperience
import dev.slne.surf.skill.api.paper.level.SkillLevel
import dev.slne.surf.skill.core.paper.ability.SkillAbility
import dev.slne.surf.skill.core.paper.level.EmptySkillLevel
import it.unimi.dsi.fastutil.objects.ObjectList
import kotlinx.coroutines.withContext
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemType
import java.util.*

@OptIn(InternalSkillApi::class)
abstract class AbstractSkill(
    override val name: String,
    override val material: ItemType,
    override val displayName: Component,
    override val lore: LoreBuilder.() -> Unit,
    override val baseExperience: Int = Skill.BASE_EXPERIENCE,
    override val maxLevel: Int = Skill.MAX_SKILL_LEVEL,
    override val maxExperience: Int = Skill.MAX_EXPERIENCE,
    override val active: Boolean = true,
    listeners: ObjectList<Listener> = objectListOf(),
    val abilities: ObjectList<SkillAbility> = objectListOf()
) : Skill {
    private val _listeners = mutableObjectListOf<Listener>(listeners)
    override val listeners get() = _listeners.freeze()

    override val experienceCurve = ExponentialExperienceCurve(
        maxLevel = maxLevel,
        maxExperience = maxExperience,
        baseExperience = baseExperience
    )

    open fun getExtraLevels(): ObjectList<SkillLevel> {
        return objectListOf()
    }

    override suspend fun awardLevelUpRewards(uuid: UUID, level: Int) {
        val player = server.getPlayer(uuid) ?: return
        val levelRewards = getLevels().firstOrNull { it.level == level }?.rewards ?: objectListOf()

        val activeAbilities = abilities.filter { it.isActiveAtLevel(level) }

        player.sendText {
            appendInfoPrefix()
            spacer("-".repeat(15))
            variableValue("LEVELUP", TextDecoration.BOLD)
            spacer("-".repeat(15))

            appendNewInfoPrefixedLine()

            appendNewInfoPrefixedLine()
            info("Du hast Level ")
            variableValue(level)
            info(" in ")
            append(displayName)
            info(" erreicht!")

            if (activeAbilities.isNotEmpty()) {
                appendNewInfoPrefixedLine()
                appendNewInfoPrefixedLine()
                info("Fähigkeiten:")

                activeAbilities.forEach { ability ->
                    val newValue = ability.getFormattedValue(level)
                    val isNew = !ability.isActiveAtLevel(level - 1)

                    appendNewInfoPrefixedLine()
                    info("  - ")
                    append(ability.displayName)
                    info(": ")

                    if (isNew) {
                        variableValue(newValue)
                        spacer(" (")
                        variableValue("NEU!")
                        spacer(")")
                    } else {
                        val oldValue = ability.getFormattedValue(level - 1)
                        spacer(oldValue)
                        info(" → ")
                        variableValue(newValue)
                    }
                }
            }

            if (levelRewards.isNotEmpty()) {
                appendNewInfoPrefixedLine()
                appendNewInfoPrefixedLine()
                info("Belohnungen:")

                levelRewards.forEach { reward ->
                    appendNewInfoPrefixedLine()
                    info("  - ")
                    append(reward.displayName)
                }
            }

            appendNewInfoPrefixedLine()

            appendNewInfoPrefixedLine()
            spacer("-".repeat(15))
            variableValue("LEVELUP", TextDecoration.BOLD)
            spacer("-".repeat(15))
        }

        player.playSound(true) {
            type(BukkitSound.ENTITY_PLAYER_LEVELUP)
            source(Sound.Source.AMBIENT)
            volume(.5f)
            pitch(.25f)
        }

        player.playSound(true) {
            type(BukkitSound.ENTITY_FIREWORK_ROCKET_BLAST)
            source(Sound.Source.AMBIENT)
            volume(.5f)
        }

        player.playSound(true) {
            type(BukkitSound.ENTITY_FIREWORK_ROCKET_TWINKLE)
            source(Sound.Source.AMBIENT)
            volume(.5f)
        }


        if (levelRewards.isEmpty()) {
            return
        }

        withContext(SkillInstance.entityDispatcher(player)) {
            levelRewards.forEach { it.grant(player) }
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

            if (abilities.isNotEmpty()) {
                emptyLine()
                line { variableKey("Fähigkeiten:") }

                abilities.forEach { ability ->
                    val isUnlocked = ability.isActiveAtLevel(currentLevel)
                    emptyLine()
                    line {
                        if (isUnlocked) success("✔ ")
                        else error("✘ ")
                        append(ability.displayName)
                        spacer(" (ab Lvl. ${ability.minLevel})")
                    }
                    if (ability.description.isNotEmpty()) {
                        line {
                            spacer("  ${ability.description}")
                        }
                    }
                    if (isUnlocked) {
                        line {
                            spacer("  Aktuell: ")
                            variableValue(ability.getFormattedValue(currentLevel))
                        }
                    }
                }
            }

            if (!active) {
                emptyLine()
                line {
                    error("Diese Fähigkeit ist derzeit nicht verfügbar")
                }
            }
        }
    }

    override fun registerListeners() {
        _listeners.forEach { it.register() }
    }

    override fun asComponent() = displayName
}