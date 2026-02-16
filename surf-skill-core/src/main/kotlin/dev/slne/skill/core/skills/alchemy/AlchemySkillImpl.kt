@file:Suppress("UnstableApiUsage")

package dev.slne.skill.core.skills.alchemy

import dev.slne.skill.core.AbstractSkill
import dev.slne.surf.skill.api.skills.AlchemySkill
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.inventory.ItemType

object AlchemySkillImpl : AbstractSkill(
    name = "alchemy",
    material = ItemType.BREWING_STAND,
    displayName = buildText {
        primary("Alchemie".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Fähigkeiten in der Alchemie zu verbessern.")
        }
    }
), AlchemySkill