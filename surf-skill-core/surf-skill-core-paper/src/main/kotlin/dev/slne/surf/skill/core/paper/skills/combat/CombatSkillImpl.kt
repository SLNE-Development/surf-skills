@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.paper.skills.combat

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.util.objectListOf
import dev.slne.surf.api.paper.builder.buildItem
import dev.slne.surf.api.paper.builder.buildLore
import dev.slne.surf.api.paper.builder.displayName
import dev.slne.surf.api.paper.builder.meta
import dev.slne.surf.skill.api.paper.level.SkillLevel
import dev.slne.surf.skill.api.paper.level.reward.rewards.LevelItemRewards
import dev.slne.surf.skill.api.paper.skills.CombatSkill
import dev.slne.surf.skill.core.paper.AbstractSkill
import dev.slne.surf.skill.core.paper.ability.SkillAbility
import dev.slne.surf.skill.core.paper.level.skillLevel
import dev.slne.surf.skill.core.paper.skills.combat.listeners.CombatAbilityListener
import dev.slne.surf.skill.core.paper.skills.combat.listeners.CombatKillListener
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemType
import org.bukkit.inventory.meta.EnchantmentStorageMeta

private val enchantedBook = buildItem(ItemType.ENCHANTED_BOOK) {
    displayName {
        primary("Großes Buch der Verzauberung")
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
        primary("Combat".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Kampffähigkeiten zu verbessern.")
        }

        line {
            spacer("Werde stärker, schneller und tödlicher im Kampf gegen Monster und Spieler.")
        }
    },
    listeners = objectListOf(CombatKillListener, CombatAbilityListener),
    abilities = objectListOf(
        SkillAbility(
            displayName = buildText { primary("Battle Hardened".toSmallCaps()) },
            description = "Verringert den erlittenen Schaden durch langjährige Kampferfahrung.",
            minLevel = 1,
            maxValue = 0.50,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Reaper's Fortune".toSmallCaps()) },
            description = "Erhöht die Chance, zusätzliche Beute beim Töten von Gegnern zu erhalten.",
            minLevel = 11,
            maxValue = 0.20,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Heavy Strike".toSmallCaps()) },
            description = "Gibt deinen Angriffen eine Chance, kritischen Bonusschaden zu verursachen.",
            minLevel = 21,
            maxValue = 0.15,
            valueFormatter = SkillAbility.percentageFormatter()
        )
    )
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