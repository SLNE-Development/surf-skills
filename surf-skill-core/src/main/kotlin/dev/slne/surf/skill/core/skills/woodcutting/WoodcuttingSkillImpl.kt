@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.skills.woodcutting

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.util.objectListOf
import dev.slne.surf.skill.api.skills.WoodcuttingSkill
import dev.slne.surf.skill.core.AbstractSkill
import dev.slne.surf.skill.core.skills.woodcutting.listeners.WoodcuttingSkillListener
import org.bukkit.inventory.ItemType

@AutoService(WoodcuttingSkill::class)
class WoodcuttingSkillImpl : AbstractSkill(
    listeners = objectListOf(WoodcuttingSkillListener),
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