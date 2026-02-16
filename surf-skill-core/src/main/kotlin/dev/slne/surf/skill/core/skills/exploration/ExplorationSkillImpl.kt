@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.skills.exploration

import dev.slne.surf.skill.api.skills.ExplorationSkill
import dev.slne.surf.skill.core.AbstractSkill
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.inventory.ItemType

object ExplorationSkillImpl : AbstractSkill(
    name = "exploration",
    material = ItemType.GLOBE_BANNER_PATTERN,
    displayName = buildText {
        primary("Erkundung".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Fähigkeiten in der Erkundung zu verbessern.")
        }
    }
), ExplorationSkill