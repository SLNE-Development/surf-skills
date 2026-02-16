@file:Suppress("UnstableApiUsage")

package dev.slne.skill.core.skills.combat

import dev.slne.skill.core.AbstractSkill
import dev.slne.skill.core.skills.combat.listeners.CombatKillListener
import dev.slne.surf.skill.api.skills.CombatSkill
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.util.objectListOf
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemType

object CombatSkillImpl : AbstractSkill(
    name = "combat",
    material = ItemType.DIAMOND_SWORD,
    displayName = buildText {
        primary("Kampf".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Fähigkeiten im Kampf zu verbessern.")
        }
    }
), CombatSkill {
    override fun registerSkillListeners(): ObjectList<Listener> {
        return objectListOf(
            CombatKillListener
        )
    }
}