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
    material = ItemType.STONE_HOE,
    displayName = buildText {
        primary("Farming".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Sammelkünste zu verbessern.")
        }

        line {
            spacer("Sammle Pflanzen, Beeren und Naturschätze effizienter und profitiere von der Natur.")
        }
    },
    listeners = objectListOf(ForagingListener, ForagingAbilityListener),
    abilities = objectListOf(
        SkillAbility(
            displayName = buildText { primary("Earth-Bound Durability".toSmallCaps()) },
            description = "Deine Verbindung zur Natur verleiht dir erhöhte Ausdauer und Widerstandsfähigkeit.",
            minLevel = 1,
            maxValue = 0.50,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Green Thumb".toSmallCaps()) },
            description = "Erhöht die Menge der gesammelten Pflanzen und natürlichen Ressourcen.",
            minLevel = 11,
            maxValue = 0.20,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Saturation".toSmallCaps()) },
            description = "Gesammelte Nahrung gibt mehr Sättigungspunkte, sodass du länger durchhältst.",
            minLevel = 21,
            maxValue = 0.60,
            valueFormatter = SkillAbility.percentageFormatter()
        )
    )
), ForagingSkill