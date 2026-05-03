@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.paper.skills.combat

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.util.objectListOf
import dev.slne.surf.skill.api.paper.level.SkillLevel
import dev.slne.surf.skill.api.paper.level.reward.rewards.LevelItemRewards
import dev.slne.surf.skill.api.paper.skills.CombatSkill
import dev.slne.surf.skill.core.paper.AbstractSkill
import dev.slne.surf.skill.core.paper.ability.SkillAbility
import dev.slne.surf.skill.core.paper.level.skillLevel
import dev.slne.surf.skill.core.paper.skills.combat.listeners.CombatAbilityListener
import dev.slne.surf.skill.core.paper.skills.combat.listeners.CombatKillListener
import dev.slne.surf.skill.core.paper.skills.rewards.enchantedBook
import dev.slne.surf.skill.core.paper.skills.rewards.enchantedItem
import dev.slne.surf.skill.core.paper.skills.rewards.potionReward
import dev.slne.surf.skill.core.paper.skills.rewards.rewardItem
import dev.slne.surf.skill.core.paper.skills.rewards.surfEnchantment
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemRarity
import org.bukkit.inventory.ItemType
import org.bukkit.potion.PotionEffectType

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
            spacer("Werde stärker, schneller und tödlicher im Kampf gegen Monster.")
        }
    },
    listeners = objectListOf(CombatKillListener, CombatAbilityListener),
    abilities = objectListOf(
        SkillAbility(
            displayName = buildText { primary("Battle Hardened".toSmallCaps()) },
            description = "Waffen und Rüstungen verlieren 0% → 50% weniger Haltbarkeit",
            minLevel = 1,
            maxValue = 0.50,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Reaper's Fortune".toSmallCaps()) },
            description = "Erhalte eine 0% → 20% Chance, 2x Drops von Monstern zu erhalten",
            minLevel = 11,
            maxValue = 0.20,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Strong Impact".toSmallCaps()) },
            description = "Verursache 0% → 15% mehr Schaden gegen feindliche Kreaturen",
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
                    add(LevelItemRewards(objectListOf(potionReward(
                        name = "Resistance Potion",
                        color = 10329495,
                        effectType = PotionEffectType.RESISTANCE,
                        amplifier = 3,
                        duration = 24000
                    ))))
                }
            ),
            skillLevel(
                skill = this,
                level = 20,
                rewards = {
                    add(LevelItemRewards(objectListOf(rewardItem(ItemType.TOTEM_OF_UNDYING))))
                }
            ),
            skillLevel(
                skill = this,
                level = 30,
                rewards = {
                    add(LevelItemRewards(objectListOf(enchantedBook(
                        enchantment = surfEnchantment("experience"),
                        level = 4,
                        special = true,
                        hideEnchantments = true
                    ))))
                }
            ),
            skillLevel(
                skill = this,
                level = 40,
                rewards = {
                    add(LevelItemRewards(objectListOf(enchantedBook(
                        enchantment = Enchantment.LOOTING,
                        level = 5,
                        special = true
                    ))))
                }
            ),
            skillLevel(
                skill = this,
                level = 50,
                rewards = {
                    add(LevelItemRewards(objectListOf(enchantedItem(
                        type = ItemType.BOW,
                        Enchantment.INFINITY to 1,
                        Enchantment.MENDING to 1,
                        rarity = ItemRarity.EPIC,
                        special = true,
                        hideEnchantments = true
                    ))))
                }
            )
        )
    }
}