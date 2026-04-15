@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.skills.enchanting

import com.google.auto.service.AutoService
import dev.slne.surf.skill.api.skills.EnchantingSkill
import dev.slne.surf.skill.core.AbstractSkill
import dev.slne.surf.skill.core.skills.enchanting.listeners.EnchantListener
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.util.objectListOf
import org.bukkit.inventory.ItemType

@AutoService(EnchantingSkill::class)
class EnchantingSkillImpl : AbstractSkill(
    name = "enchanting",
    material = ItemType.ENCHANTING_TABLE,
    displayName = buildText {
        primary("Verzauberung".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Fähigkeiten in der Verzauberung zu verbessern.")
        }
    },
    listeners = objectListOf(EnchantListener)
), EnchantingSkill