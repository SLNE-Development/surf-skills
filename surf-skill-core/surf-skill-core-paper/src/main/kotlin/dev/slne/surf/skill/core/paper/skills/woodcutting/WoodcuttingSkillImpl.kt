@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.paper.skills.woodcutting

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.util.objectListOf
import dev.slne.surf.skill.api.paper.skills.WoodcuttingSkill
import dev.slne.surf.skill.core.paper.AbstractSkill
import dev.slne.surf.skill.core.paper.ability.SkillAbility
import dev.slne.surf.skill.core.paper.skills.woodcutting.listeners.WoodcuttingAbilityListener
import dev.slne.surf.skill.core.paper.skills.woodcutting.listeners.WoodcuttingSkillListener
import org.bukkit.inventory.ItemType

@AutoService(WoodcuttingSkill::class)
class WoodcuttingSkillImpl : AbstractSkill(
    name = "woodcutting",
    material = ItemType.WOODEN_AXE,
    displayName = buildText {
        primary("Holzfällen".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Fähigkeiten im Holzfällen zu verbessern.")
        }
    },
    listeners = objectListOf(WoodcuttingAbilityListener, WoodcuttingSkillListener),
    abilities = objectListOf(
        SkillAbility(
            displayName = buildText { primary("Handwerkskunst".toSmallCaps()) },
            minLevel = 1,
            maxValue = 0.50,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Geschenk des Waldes".toSmallCaps()) },
            minLevel = 11,
            maxValue = 0.20,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Meister-Holzfäller".toSmallCaps()) },
            minLevel = 21,
            maxValue = 15.0,
            valueFormatter = SkillAbility.secondsFormatter()
        )
    )
), WoodcuttingSkill