@file:Suppress("UnstableApiUsage")

package dev.slne.skill.core.skills.foraging

import dev.slne.skill.core.AbstractSkill
import dev.slne.surf.skill.api.skills.ForagingSkill
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.inventory.ItemType

object ForagingSkillImpl : AbstractSkill(
    name = "foraging",
    material = ItemType.SWEET_BERRIES,
    displayName = buildText {
        primary("Sammeln".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Fähigkeiten im Sammeln zu verbessern.")
        }
    }
), ForagingSkill