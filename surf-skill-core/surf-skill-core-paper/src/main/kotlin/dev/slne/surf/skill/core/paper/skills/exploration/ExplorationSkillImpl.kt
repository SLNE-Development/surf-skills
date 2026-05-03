@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.paper.skills.exploration

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.util.objectListOf
import dev.slne.surf.skill.api.paper.skills.ExplorationSkill
import dev.slne.surf.skill.core.paper.AbstractSkill
import dev.slne.surf.skill.core.paper.skills.exploration.listeners.ExplorationAbilityListener
import org.bukkit.inventory.ItemType

@AutoService(ExplorationSkill::class)
class ExplorationSkillImpl : AbstractSkill(
    name = "exploration",
    niceName = "Unbekannt",
    material = ItemType.SPYGLASS,
    displayName = buildText {
        primary("???".toSmallCaps())
    },
    lore = {},
    listeners = objectListOf(ExplorationAbilityListener),
    active = false
), ExplorationSkill