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
    material = ItemType.GOLDEN_PICKAXE,
    displayName = buildText {
        primary("Mining".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Bergbaukünste zu verbessern.")
        }

        line {
            spacer("Baue schneller ab, finde wertvollere Erze und meistere die Tiefen der Erde.")
        }
    },
    listeners = objectListOf(MiningBlockListener, MiningAbilityListener),
    abilities = objectListOf(
        SkillAbility(
            displayName = buildText { primary("Reinforced Pickaxe".toSmallCaps()) },
            description = "Spitzhacken verlieren 0% → 50% weniger Haltbarkeit",
            minLevel = 1,
            maxValue = 0.50,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Cave Exploration".toSmallCaps()) },
            description = "Erhalte eine 0% → 20% Chance, 2x Drops von Erzen zu erhalten",
            minLevel = 11,
            maxValue = 0.20,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Dynamic Mining".toSmallCaps()) },
            description = "Erhalte eine 0% → 1% Chance beim Abbauen von Stein oder Deepslate für 3 → 12 Sekunden Haste VIII zu erhalten",
            minLevel = 21,
            maxValue = 0.01,
            valueFormatter = SkillAbility.percentageFormatter()
        )
    )
), MiningSkill