@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.paper.skills.fishing

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.util.objectListOf
import dev.slne.surf.skill.api.paper.skills.FishingSkill
import dev.slne.surf.skill.core.paper.AbstractSkill
import dev.slne.surf.skill.core.paper.ability.SkillAbility
import dev.slne.surf.skill.core.paper.skills.fishing.listeners.FishingAbilityListener
import dev.slne.surf.skill.core.paper.skills.fishing.listeners.FishingSkillListener
import org.bukkit.inventory.ItemType

@AutoService(FishingSkill::class)
class FishingSkillImpl : AbstractSkill(
    listeners = objectListOf(FishingSkillListener, FishingAbilityListener),
    name = "fishing",
    material = ItemType.FISHING_ROD,
    displayName = buildText {
        primary("Fishing".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Angelkünste zu verbessern.")
        }

        line {
            spacer("Angle seltene Schätze aus den Tiefen der Ozeane und Flüsse.")
        }
    },
    abilities = objectListOf(
        SkillAbility(
            displayName = buildText { primary("Magnetic Rod".toSmallCaps()) },
            description = "Erhöht die Chance, beim Angeln Schätze und seltene Gegenstände zu finden.",
            minLevel = 1,
            maxValue = 0.25,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Neptune's Favor".toSmallCaps()) },
            description = "Neptun selbst segnet deine Angel – Fische beißen öfter und schneller an.",
            minLevel = 11,
            maxValue = 0.20,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Larger Lungs".toSmallCaps()) },
            description = "Erhöht deine Atemzeit unter Wasser, um schwer erreichbare Angelplätze zu erkunden.",
            minLevel = 21,
            maxValue = 3.00,
            valueFormatter = { value -> "%.2f%%".format(value / (1.0 + value) * 100) }
        )
    )
), FishingSkill