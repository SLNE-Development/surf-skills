@file:Suppress("UnstableApiUsage")

package dev.slne.skill.core.skills.fishing

import dev.slne.skill.core.AbstractSkill
import dev.slne.surf.skill.api.skills.FishingSkill
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.inventory.ItemType

object FishingSkillImpl : AbstractSkill(
    name = "fishing",
    material = ItemType.FISHING_ROD,
    displayName = buildText {
        primary("Fischen".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Fähigkeiten im Fischen zu verbessern.")
        }
    }
), FishingSkill