@file:Suppress("UnstableApiUsage")

package dev.slne.skill.core.skills.woodcutting

import dev.slne.skill.core.AbstractSkill
import dev.slne.surf.skill.api.skills.WoodcuttingSkill
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.inventory.ItemType

object WoodcuttingSkillImpl : AbstractSkill(
    name = "foraging",
    material = ItemType.WOODEN_AXE,
    displayName = buildText {
        primary("Holzfällen".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Fähigkeiten im Holzfällen zu verbessern.")
        }
    }
), WoodcuttingSkill