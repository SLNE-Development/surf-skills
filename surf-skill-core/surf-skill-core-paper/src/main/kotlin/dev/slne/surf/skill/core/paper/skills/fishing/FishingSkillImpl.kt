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
        primary("Fischen".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Fähigkeiten im Fischen zu verbessern.")
        }
    },
    abilities = objectListOf(
        SkillAbility(
            displayName = buildText { primary("Magnetische Angel".toSmallCaps()) },
            minLevel = 1,
            maxValue = 0.25,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Neptuns Gunst".toSmallCaps()) },
            minLevel = 11,
            maxValue = 0.20,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Größere Lungen".toSmallCaps()) },
            minLevel = 21,
            maxValue = 3.00,
            valueFormatter = SkillAbility.percentageFormatter()
        )
    )
), FishingSkill