@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.paper.skills.exploration

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.util.objectListOf
import dev.slne.surf.skill.api.paper.Skills.ExplorationSkill
import dev.slne.surf.skill.core.paper.AbstractSkill
import dev.slne.surf.skill.core.paper.skills.exploration.listeners.ExplorationAbilityListener
import org.bukkit.inventory.ItemType

@AutoService(ExplorationSkill::class)
class ExplorationSkillImpl : AbstractSkill(
    name = "exploration",
    material = ItemType.GLOBE_BANNER_PATTERN,
    displayName = buildText {
        primary("Erkundung".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Fähigkeiten in der Erkundung zu verbessern.")
        }
    },
    listeners = objectListOf(ExplorationAbilityListener)
), ExplorationSkill