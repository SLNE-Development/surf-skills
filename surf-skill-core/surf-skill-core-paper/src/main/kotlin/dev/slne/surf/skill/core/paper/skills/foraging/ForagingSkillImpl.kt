@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.paper.skills.foraging

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.util.objectListOf
import dev.slne.surf.skill.api.paper.level.SkillLevel
import dev.slne.surf.skill.api.paper.level.reward.rewards.LevelItemRewards
import dev.slne.surf.skill.api.paper.skills.ForagingSkill
import dev.slne.surf.skill.core.paper.AbstractSkill
import dev.slne.surf.skill.core.paper.ability.SkillAbility
import dev.slne.surf.skill.core.paper.level.skillLevel
import dev.slne.surf.skill.core.paper.skills.foraging.listeners.ForagingAbilityListener
import dev.slne.surf.skill.core.paper.skills.foraging.listeners.ForagingListener
import dev.slne.surf.skill.core.paper.skills.rewards.enchantedBook
import dev.slne.surf.skill.core.paper.skills.rewards.potionReward
import dev.slne.surf.skill.core.paper.skills.rewards.surfEnchantment
import dev.slne.surf.skill.core.paper.skills.rewards.suspiciousStewReward
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemType
import org.bukkit.potion.PotionEffectType

@AutoService(ForagingSkill::class)
class ForagingSkillImpl : AbstractSkill(
    name = "foraging",
    niceName = "farming",
    material = ItemType.STONE_HOE,
    displayName = buildText {
        primary("Farming".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Sammel und Erntekünste zu verbessern.")
        }

        line {
            spacer("Sammle Pflanzen, Beeren und Naturschätze effizienter und profitiere von der Natur.")
        }
    },
    listeners = objectListOf(ForagingListener, ForagingAbilityListener),
    abilities = objectListOf(
        SkillAbility(
            displayName = buildText { primary("Earth-Bound Durability".toSmallCaps()) },
            description = "Schaufeln und Hacken verlieren 0% → 50% weniger Haltbarkeit",
            minLevel = 1,
            maxValue = 0.50,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Green Thumb".toSmallCaps()) },
            description = "Erhalte eine 0% → 20% Chance, 2x Drops von Nutzpflanzen zu erhalten",
            minLevel = 11,
            maxValue = 0.20,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Satiation".toSmallCaps()) },
            description = "Verliere 0% → 60% weniger Hunger",
            minLevel = 21,
            maxValue = 0.60,
            valueFormatter = SkillAbility.percentageFormatter()
        )
    )
), ForagingSkill {
    override fun getExtraLevels(): ObjectList<SkillLevel> {
        return objectListOf(
            skillLevel(
                skill = this,
                level = 10,
                rewards = {
                    add(LevelItemRewards(objectListOf(suspiciousStewReward(
                        loreText = "Saturation (02:00:00)",
                        effectType = PotionEffectType.SATURATION,
                        duration = 1
                    ))))
                }
            ),
            skillLevel(
                skill = this,
                level = 20,
                rewards = {
                    add(LevelItemRewards(objectListOf(potionReward(
                        name = "Haste Potion",
                        color = 16701501,
                        effectType = PotionEffectType.HASTE,
                        amplifier = 9,
                        duration = 24000
                    ))))
                }
            ),
            skillLevel(
                skill = this,
                level = 30,
                rewards = {
                    add(LevelItemRewards(objectListOf(enchantedBook(
                        enchantment = surfEnchantment("mending"),
                        level = 1
                    ))))
                }
            ),
            skillLevel(
                skill = this,
                level = 40,
                rewards = {
                    add(LevelItemRewards(objectListOf(enchantedBook(
                        enchantment = Enchantment.EFFICIENCY,
                        level = 6,
                        special = true
                    ))))
                }
            ),
            skillLevel(
                skill = this,
                level = 50,
                rewards = {
                    add(LevelItemRewards(objectListOf(enchantedBook(
                        enchantment = Enchantment.FEATHER_FALLING,
                        level = 7,
                        special = true
                    ))))
                }
            )
        )
    }
}