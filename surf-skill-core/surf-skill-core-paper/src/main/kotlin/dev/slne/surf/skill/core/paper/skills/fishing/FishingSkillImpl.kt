@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.paper.skills.fishing

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.builder.colors.SpacerComponentBuilderColor.spacer
import dev.slne.surf.api.core.util.objectListOf
import dev.slne.surf.skill.api.skills.FishingSkill
import dev.slne.surf.skill.core.paper.AbstractSkill
import dev.slne.surf.skill.core.paper.skills.fishing.listeners.FishingAbilityListener
import dev.slne.surf.skill.core.paper.skills.fishing.listeners.FishingSkillListener
import org.bukkit.inventory.ItemType

@AutoService(FishingSkill::class)
class FishingSkillImpl : AbstractSkill(
    listeners = objectListOf(FishingSkillListener, FishingAbilityListener),
    name = "fishing",
    material = ItemType.FISHING_ROD,
    displayName = buildText {
        primary("Fischen".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Fähigkeiten im Fischen zu verbessern.")
        }
    }
), FishingSkill