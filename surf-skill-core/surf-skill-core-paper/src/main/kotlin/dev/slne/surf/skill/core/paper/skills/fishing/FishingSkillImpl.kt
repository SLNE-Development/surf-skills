@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.paper.skills.fishing

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.util.objectListOf
import dev.slne.surf.skill.api.common.curve.curves.StaticExperienceCurveSmall
import dev.slne.surf.skill.api.paper.level.SkillLevel
import dev.slne.surf.skill.api.paper.level.reward.rewards.LevelItemRewards
import dev.slne.surf.skill.api.paper.skills.FishingSkill
import dev.slne.surf.skill.core.paper.AbstractSkill
import dev.slne.surf.skill.core.paper.ability.SkillAbility
import dev.slne.surf.skill.core.paper.level.skillLevel
import dev.slne.surf.skill.core.paper.skills.fishing.listeners.FishingAbilityListener
import dev.slne.surf.skill.core.paper.skills.fishing.listeners.FishingSkillListener
import dev.slne.surf.skill.core.paper.skills.rewards.enchantedBook
import dev.slne.surf.skill.core.paper.skills.rewards.potionReward
import dev.slne.surf.skill.core.paper.skills.rewards.rewardItem
import dev.slne.surf.skill.core.paper.skills.rewards.surfEnchantment
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemType
import org.bukkit.potion.PotionEffectType

@AutoService(FishingSkill::class)
class FishingSkillImpl : AbstractSkill(
    listeners = objectListOf(FishingSkillListener, FishingAbilityListener),
    name = "fishing",
    material = ItemType.FISHING_ROD,
    displayName = buildText {
        primary("Fishing".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Angelkünste zu verbessern.")
        }

        line {
            spacer("Angle seltene Schätze aus den Tiefen der Ozeane und Flüsse.")
        }
    },
    abilities = objectListOf(
        SkillAbility(
            displayName = buildText { primary("Magnetic Rod".toSmallCaps()) },
            description = "Fische beißen 0% → 25% schneller an",
            minLevel = 1,
            maxValue = 0.25,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Neptune's Favor".toSmallCaps()) },
            description = "Erhalte eine 0% → 20% Chance, 2x Drops von Meerestieren zu erhalten",
            minLevel = 11,
            maxValue = 0.20,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Bigger Lungs".toSmallCaps()) },
            description = "Du kannst unter Wasser 0% → 300% länger atmen",
            minLevel = 21,
            maxValue = 3.00,
            valueFormatter = SkillAbility.percentageFormatter()
        )
    ),
    experienceCurve = StaticExperienceCurveSmall()
), FishingSkill {
    override fun getExtraLevels(): ObjectList<SkillLevel> {
        return objectListOf(
            skillLevel(
                skill = this,
                level = 10,
                rewards = {
                    add(LevelItemRewards(objectListOf(potionReward(
                        name = "Dolphin's Grace Potion",
                        color = 3847130,
                        effectType = PotionEffectType.DOLPHINS_GRACE,
                        amplifier = 3,
                        duration = 24000
                    ))))
                }
            ),
            skillLevel(
                skill = this,
                level = 20,
                rewards = {
                    add(LevelItemRewards(objectListOf(rewardItem(ItemType.HEART_OF_THE_SEA))))
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
                        enchantment = Enchantment.LURE,
                        level = 4,
                        special = true
                    ))))
                }
            ),
            skillLevel(
                skill = this,
                level = 50,
                rewards = {
                    add(LevelItemRewards(objectListOf(enchantedBook(
                        enchantment = Enchantment.RIPTIDE,
                        level = 6,
                        special = true,
                        hideEnchantments = true
                    ))))
                }
            )
        )
    }
}