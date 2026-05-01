@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.paper.skills.woodcutting

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.util.objectListOf
import dev.slne.surf.api.core.util.toObjectList
import dev.slne.surf.skill.api.paper.level.SkillLevel
import dev.slne.surf.skill.api.paper.level.reward.rewards.LevelItemRewards
import dev.slne.surf.skill.api.paper.level.reward.rewards.LumberjackUpgradeLevelReward
import dev.slne.surf.skill.api.paper.skills.WoodcuttingSkill
import dev.slne.surf.skill.core.paper.AbstractSkill
import dev.slne.surf.skill.core.paper.ability.SkillAbility
import dev.slne.surf.skill.core.paper.level.skillLevel
import dev.slne.surf.skill.core.paper.skills.rewards.efficiencyBook
import dev.slne.surf.skill.core.paper.skills.rewards.hastePotion
import dev.slne.surf.skill.core.paper.skills.rewards.ironBlocks
import dev.slne.surf.skill.core.paper.skills.rewards.soulboundBook
import dev.slne.surf.skill.core.paper.skills.woodcutting.listeners.WoodcuttingAbilityListener
import dev.slne.surf.skill.core.paper.skills.woodcutting.listeners.WoodcuttingSkillListener
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.inventory.ItemType

@AutoService(WoodcuttingSkill::class)
class WoodcuttingSkillImpl : AbstractSkill(
    name = "woodcutting",
    material = ItemType.COPPER_AXE,
    displayName = buildText {
        primary("Woodcutting".toSmallCaps())
    },
    lore = {
        line {
            spacer("Dieser Skill ermöglicht es dir, deine Holzfällerkünste zu verbessern.")
        }

        line {
            spacer("Fälle Bäume schneller, erhalte mehr Ressourcen und beherrsche die Kunst des Holzfällens.")
        }
    },
    listeners = objectListOf(WoodcuttingAbilityListener, WoodcuttingSkillListener),
    abilities = objectListOf(
        SkillAbility(
            displayName = buildText { primary("Craftsmanship".toSmallCaps()) },
            description = "Äxte verlieren 0% → 50% weniger Haltbarkeit",
            minLevel = 1,
            maxValue = 0.50,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Forest's Gift".toSmallCaps()) },
            description = "Erhalte eine 0% → 20% Chance, 2x Drops von geerntetem Holz zu erhalten",
            minLevel = 11,
            maxValue = 0.20,
            valueFormatter = SkillAbility.percentageFormatter()
        ),
        SkillAbility(
            displayName = buildText { primary("Master Lumberjack".toSmallCaps()) },
            description = "Verringert den Cooldown des Lumberjack Enchantments um 0 → 15 Sekunden",
            minLevel = 21,
            maxValue = 15.0,
            valueFormatter = SkillAbility.secondsFormatter()
        )
    ),
), WoodcuttingSkill {
    override fun getExtraLevels(): ObjectList<SkillLevel> = buildList {
        add(
            skillLevel(
                skill = this@WoodcuttingSkillImpl,
                level = 10,
                rewards = {
                    add(LevelItemRewards(objectListOf(ironBlocks())))
                }
            )
        )

        add(
            skillLevel(
                skill = this@WoodcuttingSkillImpl,
                level = 20,
                rewards = {
                    add(LevelItemRewards(objectListOf(hastePotion())))
                }
            )
        )

        for (level in 21..50) {
            add(
                skillLevel(
                    skill = this@WoodcuttingSkillImpl,
                    level = level,
                    rewards = {
                        add(
                            LumberjackUpgradeLevelReward(
                                WoodcuttingAbilityListener.getLumberjackCooldownReduction(
                                    level
                                )
                            )
                        )

                        when (level) {
                            30 -> add(LevelItemRewards(objectListOf(soulboundBook())))
                            40 -> add(LevelItemRewards(objectListOf(efficiencyBook(6))))
                            50 -> add(LevelItemRewards(objectListOf(efficiencyBook(7))))
                        }
                    }
                ))
        }
    }.toObjectList()
}