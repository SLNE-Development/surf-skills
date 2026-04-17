@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.paper.skills.enchanting

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.util.objectListOf
import dev.slne.surf.skill.api.paper.skills.EnchantingSkill
import dev.slne.surf.skill.core.paper.AbstractSkill
import dev.slne.surf.skill.core.paper.ability.SkillAbility
import dev.slne.surf.skill.core.paper.skills.enchanting.listeners.EnchantListener
import dev.slne.surf.skill.core.paper.skills.enchanting.listeners.EnchantingAbilityListener
import org.bukkit.inventory.ItemType

@AutoService(EnchantingSkill::class)
class EnchantingSkillImpl : AbstractSkill(
    name = "enchanting",
    material = ItemType.ENCHANTING_TABLE,
    displayName = buildText {
        primary("Enchanting".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Fähigkeiten in der Verzauberungskunst zu verbessern.")
        }

        line {
            spacer("Entfalte die Macht arkaner Runen und verleihe deiner Ausrüstung übermenschliche Kräfte.")
        }
    },
    listeners = objectListOf(EnchantListener, EnchantingAbilityListener),
    abilities = objectListOf(
        SkillAbility(
            displayName = buildText { primary("Enchanter's Insight".toSmallCaps()) },
            description = "Senkt die benötigten Erfahrungslevel beim Verzaubern von Gegenständen.",
            minLevel = 1,
            maxValue = 0.50,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Arcane Amplification".toSmallCaps()) },
            description = "Erhöht die Stärke von Verzauberungen, die auf Gegenstände angewendet werden.",
            minLevel = 11,
            maxValue = 0.20,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Mana Pool".toSmallCaps()) },
            description = "Vergrößert den Manapool, sodass stärkere Verzauberungen möglich werden.",
            minLevel = 21,
            maxValue = 0.25,
            valueFormatter = SkillAbility.percentageFormatter()
        )
    ),
    active = false
), EnchantingSkill