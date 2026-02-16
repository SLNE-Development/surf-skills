@file:Suppress("UnstableApiUsage")

package dev.slne.skill.core.skills.combat

import dev.slne.skill.core.AbstractSkill
import dev.slne.surf.skill.api.skills.CombatSkill
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.inventory.ItemType

object CombatSkillImpl : AbstractSkill(
    name = "combat",
    material = ItemType.DIAMOND_SWORD,
    displayName = buildText {
        primary("Kampf".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Fähigkeiten im Kampf zu verbessern.")
        }
    }
), CombatSkill