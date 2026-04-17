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
            description = "Fische beißen 0% → 25% schneller an",
            minLevel = 1,
            maxValue = 0.25,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Neptune's Favor".toSmallCaps()) },
            description = "Erhalte eine 0% → 20% Chance, 2x Drops von Meerestieren zu erhalten",
            minLevel = 11,
            maxValue = 0.20,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Bigger Lungs".toSmallCaps()) },
            description = "Du kannst unter Wasser 0% → 300% länger atmen",
            minLevel = 21,
            maxValue = 3.00,
            valueFormatter = { value -> "%.2f%%".format(value / (1.0 + value) * 100) }
        )
    )
), FishingSkill