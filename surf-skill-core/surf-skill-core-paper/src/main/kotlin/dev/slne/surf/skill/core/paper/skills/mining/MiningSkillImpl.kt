@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.paper.skills.mining

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.builder.colors.SpacerComponentBuilderColor.spacer
import dev.slne.surf.api.core.util.objectListOf
import dev.slne.surf.skill.api.skills.MiningSkill
import dev.slne.surf.skill.core.paper.AbstractSkill
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
    listeners = objectListOf(MiningBlockListener, MiningAbilityListener)
), MiningSkill