@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.paper.skills.alchemy

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.util.objectListOf
import dev.slne.surf.skill.api.paper.skills.AlchemySkill
import dev.slne.surf.skill.core.paper.AbstractSkill
import dev.slne.surf.skill.core.paper.skills.alchemy.listeners.AlchemyAbilityListener
import dev.slne.surf.skill.core.paper.skills.alchemy.listeners.AlchemyListener
import org.bukkit.inventory.ItemType

@AutoService(AlchemySkill::class)
class AlchemySkillImpl : AbstractSkill(
    name = "alchemy",
    material = ItemType.BREWING_STAND,
    displayName = buildText {
        primary("Alchemy".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Fähigkeiten in der Alchemie zu verbessern.")
        }

        line {
            spacer("Braue mächtige Tränke und steigere deine Erfahrung mit jedem Level.")
        }
    },
    listeners = objectListOf(AlchemyListener, AlchemyAbilityListener),
    abilities = objectListOf(),
    active = false
), AlchemySkill