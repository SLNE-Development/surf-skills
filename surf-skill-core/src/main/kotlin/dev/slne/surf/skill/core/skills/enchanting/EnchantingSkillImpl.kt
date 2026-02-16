@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.skills.enchanting

import dev.slne.surf.skill.api.skills.EnchantingSkill
import dev.slne.surf.skill.core.AbstractSkill
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.inventory.ItemType

object EnchantingSkillImpl : AbstractSkill(
    name = "enchanting",
    material = ItemType.ENCHANTING_TABLE,
    displayName = buildText {
        primary("Verzauberung".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Fähigkeiten in der Verzauberung zu verbessern.")
        }
    }
), EnchantingSkill