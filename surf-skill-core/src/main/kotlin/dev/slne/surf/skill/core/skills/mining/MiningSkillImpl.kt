@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.skills.mining

import dev.slne.surf.skill.api.skills.MiningSkill
import dev.slne.surf.skill.core.AbstractSkill
import dev.slne.surf.skill.core.skills.mining.listeners.MiningBlockListener
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemType

object MiningSkillImpl : AbstractSkill(
    name = "mining",
    material = ItemType.COBBLESTONE,
    displayName = buildText {
        primary("Bergbau".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Fähigkeiten im Bergbau zu verbessern.")
        }
    }
), MiningSkill {
    override fun registerSkillListeners(): ObjectList<Listener> = mutableObjectListOf(
        MiningBlockListener
    )
}