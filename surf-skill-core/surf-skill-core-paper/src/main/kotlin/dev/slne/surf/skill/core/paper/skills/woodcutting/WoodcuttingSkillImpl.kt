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
    material = ItemType.COPPER_AXE,
    displayName = buildText {
        primary("Woodcutting".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Holzfällerkünste zu verbessern.")
        }

        line {
            spacer("Fälle Bäume schneller, erhalte mehr Ressourcen und beherrsche die Kunst des Holzfällens.")
        }
    },
    listeners = objectListOf(WoodcuttingAbilityListener, WoodcuttingSkillListener),
    abilities = objectListOf(
        SkillAbility(
            displayName = buildText { primary("Craftsmanship".toSmallCaps()) },
            description = "Deine Handwerkskunst steigert die Qualität der gefällten Hölzer.",
            minLevel = 1,
            maxValue = 0.50,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Gift of the Forest".toSmallCaps()) },
            description = "Der Wald selbst belohnt dich – Bäume hinterlassen beim Fällen mehr Ressourcen.",
            minLevel = 11,
            maxValue = 0.20,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Master Lumberjack".toSmallCaps()) },
            description = "Als Meister-Holzfäller fällst du Bäume in Rekordzeit.",
            minLevel = 21,
            maxValue = 15.0,
            valueFormatter = SkillAbility.secondsFormatter()
        )
    )
), WoodcuttingSkill