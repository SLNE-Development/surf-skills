@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.paper.skills.combat

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.builder.colors.PrimaryComponentBuilderColor.primary
import dev.slne.surf.api.core.messages.builder.colors.SpacerComponentBuilderColor.spacer
import dev.slne.surf.api.core.util.objectListOf
import dev.slne.surf.api.paper.builder.buildItem
import dev.slne.surf.api.paper.builder.buildLore
import dev.slne.surf.api.paper.builder.displayName
import dev.slne.surf.api.paper.builder.meta
import dev.slne.surf.skill.api.level.SkillLevel
import dev.slne.surf.skill.api.level.reward.rewards.LevelItemRewards
import dev.slne.surf.skill.api.skills.CombatSkill
import dev.slne.surf.skill.core.paper.AbstractSkill
import dev.slne.surf.skill.core.paper.level.skillLevel
import dev.slne.surf.skill.core.paper.skills.combat.listeners.CombatAbilityListener
import dev.slne.surf.skill.core.paper.skills.combat.listeners.CombatKillListener
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
    listeners = objectListOf(CombatKillListener, CombatAbilityListener)
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