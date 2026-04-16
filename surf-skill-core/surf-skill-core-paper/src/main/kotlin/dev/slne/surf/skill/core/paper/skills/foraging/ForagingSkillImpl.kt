@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.paper.skills.foraging

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.util.objectListOf
import dev.slne.surf.skill.api.paper.skills.ForagingSkill
import dev.slne.surf.skill.core.paper.AbstractSkill
import dev.slne.surf.skill.core.paper.ability.SkillAbility
import dev.slne.surf.skill.core.paper.skills.foraging.listeners.ForagingAbilityListener
import dev.slne.surf.skill.core.paper.skills.foraging.listeners.ForagingListener
import org.bukkit.inventory.ItemType

@AutoService(ForagingSkill::class)
class ForagingSkillImpl : AbstractSkill(
    name = "foraging",
    material = ItemType.SWEET_BERRIES,
    displayName = buildText {
        primary("Sammeln".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Fähigkeiten im Sammeln zu verbessern.")
        }
    },
    listeners = objectListOf(ForagingListener, ForagingAbilityListener),
    abilities = objectListOf(
        SkillAbility(
            displayName = buildText { primary("Erdverbundene Haltbarkeit".toSmallCaps()) },
            minLevel = 1,
            maxValue = 0.50,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Grüner Daumen".toSmallCaps()) },
            minLevel = 11,
            maxValue = 0.20,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Sättigung".toSmallCaps()) },
            minLevel = 21,
            maxValue = 0.60,
            valueFormatter = SkillAbility.percentageFormatter()
        )
    )
), ForagingSkill