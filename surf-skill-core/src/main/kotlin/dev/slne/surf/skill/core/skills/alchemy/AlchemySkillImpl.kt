@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.skills.alchemy

import com.google.auto.service.AutoService
import dev.slne.surf.skill.api.skills.AlchemySkill
import dev.slne.surf.skill.core.AbstractSkill
import dev.slne.surf.skill.core.skills.alchemy.listeners.AlchemyListener
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.util.objectListOf
import org.bukkit.inventory.ItemType

@AutoService(AlchemySkill::class)
class AlchemySkillImpl : AbstractSkill(
    name = "alchemy",
    material = ItemType.BREWING_STAND,
    displayName = buildText {
        primary("Alchemie".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Fähigkeiten in der Alchemie zu verbessern.")
        }
    },
    listeners = objectListOf(AlchemyListener)
), AlchemySkill