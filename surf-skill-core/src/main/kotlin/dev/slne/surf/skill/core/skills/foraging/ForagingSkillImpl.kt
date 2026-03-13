@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.skills.foraging

import com.google.auto.service.AutoService
import dev.slne.surf.skill.api.skills.ForagingSkill
import dev.slne.surf.skill.core.AbstractSkill
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.inventory.ItemType

@AutoService(ForagingSkill::class)
class ForagingSkillImpl : AbstractSkill(
    name = "foraging",
    material = ItemType.SWEET_BERRIES,
    displayName = buildText {
        primary("Sammeln".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Fähigkeiten im Sammeln zu verbessern.")
        }
    }
), ForagingSkill