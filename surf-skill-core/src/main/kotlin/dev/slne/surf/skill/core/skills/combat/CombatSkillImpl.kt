@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.skills.combat

import com.google.auto.service.AutoService
import dev.slne.surf.skill.api.level.SkillLevel
import dev.slne.surf.skill.api.level.reward.rewards.LevelItemRewards
import dev.slne.surf.skill.api.skills.CombatSkill
import dev.slne.surf.skill.core.AbstractSkill
import dev.slne.surf.skill.core.level.skillLevel
import dev.slne.surf.skill.core.skills.combat.listeners.CombatKillListener
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.builder.meta
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.util.objectListOf
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemType
import org.bukkit.inventory.meta.EnchantmentStorageMeta

private val enchantedBook = buildItem(ItemType.ENCHANTED_BOOK) {
    displayName {
        primary("Tolles Buch der Verzauberung")
    }
    buildLore {
        line {
            spacer("Dieses Buch enthält eine mächtige Verzauberung, die dir im Kampf helfen wird.")
        }
    }
    meta<EnchantmentStorageMeta> {
        addStoredEnchant(Enchantment.FORTUNE, 5, true)
    }
}

@AutoService(CombatSkill::class)
class CombatSkillImpl : AbstractSkill(
    name = "combat",
    material = ItemType.DIAMOND_SWORD,
    displayName = buildText {
        primary("Kampf".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Fähigkeiten im Kampf zu verbessern.")
        }
    },
    listeners = objectListOf(CombatKillListener)
), CombatSkill {
    override fun getExtraLevels(): ObjectList<SkillLevel> {
        return objectListOf(
            skillLevel(
                skill = this,
                level = 10,
                rewards = {
                    add(LevelItemRewards(objectListOf(enchantedBook)))
                }
            )
        )
    }
}