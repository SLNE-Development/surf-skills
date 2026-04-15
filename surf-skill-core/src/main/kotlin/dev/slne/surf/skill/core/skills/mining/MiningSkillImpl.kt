@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.skills.mining

import com.google.auto.service.AutoService
import dev.slne.surf.skill.api.skills.MiningSkill
import dev.slne.surf.skill.core.AbstractSkill
import dev.slne.surf.skill.core.skills.mining.listeners.MiningBlockListener
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.util.objectListOf
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
    listeners = objectListOf(MiningBlockListener)
), MiningSkill