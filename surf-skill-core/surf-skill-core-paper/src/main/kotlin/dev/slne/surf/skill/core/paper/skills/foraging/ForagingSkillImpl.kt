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
import dev.slne.surf.skill.core.paper.skills.rewards.efficiencyBook
import dev.slne.surf.skill.core.paper.skills.rewards.featherFallingBook
import dev.slne.surf.skill.core.paper.skills.rewards.hastePotion
import dev.slne.surf.skill.core.paper.skills.rewards.saturationStew
import dev.slne.surf.skill.core.paper.skills.rewards.surfMendingBook
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.inventory.ItemType

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
                    add(LevelItemRewards(objectListOf(saturationStew())))
                }
            ),
            skillLevel(
                skill = this,
                level = 20,
                rewards = {
                    add(LevelItemRewards(objectListOf(hastePotion())))
                }
            ),
            skillLevel(
                skill = this,
                level = 30,
                rewards = {
                    add(LevelItemRewards(objectListOf(surfMendingBook())))
                }
            ),
            skillLevel(
                skill = this,
                level = 40,
                rewards = {
                    add(LevelItemRewards(objectListOf(efficiencyBook(6))))
                }
            ),
            skillLevel(
                skill = this,
                level = 50,
                rewards = {
                    add(LevelItemRewards(objectListOf(featherFallingBook())))
                }
            )
        )
    }
}