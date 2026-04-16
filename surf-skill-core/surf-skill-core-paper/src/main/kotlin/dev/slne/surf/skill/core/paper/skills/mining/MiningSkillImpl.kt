@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.paper.skills.mining

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.util.objectListOf
import dev.slne.surf.skill.api.paper.skills.MiningSkill
import dev.slne.surf.skill.core.paper.AbstractSkill
import dev.slne.surf.skill.core.paper.ability.SkillAbility
import dev.slne.surf.skill.core.paper.skills.mining.listeners.MiningAbilityListener
import dev.slne.surf.skill.core.paper.skills.mining.listeners.MiningBlockListener
import org.bukkit.inventory.ItemType

@AutoService(MiningSkill::class)
class MiningSkillImpl : AbstractSkill(
    name = "mining",
    material = ItemType.COBBLESTONE,
    displayName = buildText {
        primary("Bergbau".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Fähigkeiten im Bergbau zu verbessern.")
        }
    },
    listeners = objectListOf(MiningBlockListener, MiningAbilityListener),
    abilities = objectListOf(
        SkillAbility(
            displayName = buildText { primary("Geschicktes Extrahieren".toSmallCaps()) },
            minLevel = 1,
            maxValue = 0.50,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Höhlenforschung".toSmallCaps()) },
            minLevel = 11,
            maxValue = 0.20,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Dynamischer Bergbau".toSmallCaps()) },
            minLevel = 21,
            maxValue = 0.01,
            valueFormatter = SkillAbility.percentageFormatter()
        )
    )
), MiningSkill