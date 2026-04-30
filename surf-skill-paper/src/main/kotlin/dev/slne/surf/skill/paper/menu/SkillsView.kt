package dev.slne.surf.skill.paper.menu

import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.paper.builder.displayName
import dev.slne.surf.api.paper.inventory.framework.dsl.layout
import dev.slne.surf.api.paper.inventory.framework.dsl.layoutSlot
import dev.slne.surf.api.paper.inventory.framework.dsl.onItemClick
import dev.slne.surf.api.paper.inventory.framework.dsl.openForPlayer
import dev.slne.surf.api.paper.inventory.framework.view.*
import dev.slne.surf.api.paper.inventory.framework.view.container.dsl.blockRow
import dev.slne.surf.api.paper.inventory.framework.view.icon.ViewIconColor
import dev.slne.surf.api.paper.inventory.framework.view.icon.ViewIconType
import dev.slne.surf.api.paper.inventory.framework.view.icon.viewIcon
import dev.slne.surf.api.paper.inventory.framework.view.state.get
import dev.slne.surf.api.paper.inventory.framework.view.state.initialState
import dev.slne.surf.skill.api.paper.Skill
import dev.slne.surf.skill.api.paper.experience.SkillExperience
import dev.slne.surf.skill.api.paper.manager.SkillManager
import dev.slne.surf.skill.api.paper.skills.*
import dev.slne.surf.skill.core.paper.experience.SkillExperienceImpl
import dev.slne.surf.skill.paper.menu.utils.MenuHeads
import it.unimi.dsi.fastutil.objects.ObjectList
import me.devnatan.inventoryframework.context.RenderContext
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.reflect.KClass


val skillsView = surfView("Skills") {
    val playerUuidState = initialState<UUID>("player_uuid")
    val skillExperienceState = initialState<ObjectList<SkillExperience>>("skill_progress")

    settings {
        navigateBackOnOutsideClick(false)
    }

    containerDefaults {
        blockRow(1)
        blockRow(2, exemptColumns = intArrayOf(1, 3, 5, 7))
        blockRow(3)
        blockRow(4, exemptColumns = intArrayOf(1, 3, 5, 7))
        blockRow(5)
    }

    onInit {
        layout {
            empty()
            row(" C M W F ")
            empty()
            row(" I E A N ")
            row("    X    ")
        }
    }

    fun <S : Skill> RenderContext.buildSkillItem(
        skill: S,
    ): Pair<SkillExperience, ItemStack> {
        val playerUuid = playerUuidState[this]

        val skillProgresses = skillExperienceState[this]
        val skillProgressWithSkill =
            skillProgresses.firstOrNull { skill.javaClass.isInstance(it.skill) }
                ?: run {
                    SkillExperienceImpl(
                        uuid = playerUuid,
                        skill = skill,
                        currentExperience = 0
                    )
                }

        return skillProgressWithSkill to skillProgressWithSkill.skill.displayItemStack(
            skillProgressWithSkill
        )
    }

    fun <S : Skill> RenderContext.renderSlot(
        skillClass: KClass<S>,
        slot: Char,
    ) {
        val skill = SkillManager.getSkill(skillClass) ?: return
        val skillItem = buildSkillItem(skill)

        layoutSlot(slot, skillItem.second)
            .onItemClick {
                openForPlayer(skillView, mapOf("skill_progress" to skillItem.first))
            }
    }

    onFirstRender {
        layoutSlot('X') {
            withItem(MenuHeads.CROSS.apply {
                displayName {
                    primary("Schliessen".toSmallCaps())
                }
            })
        }

        renderSlot(MiningSkill::class, 'M')
        renderSlot(CombatSkill::class, 'C')
        renderSlot(FarmingSkill::class, 'F')
        renderSlot(AlchemySkill::class, 'A')

        renderSlot(WoodcuttingSkill::class, 'W')
        renderSlot(ExplorationSkill::class, 'E')
        renderSlot(FishingSkill::class, 'I')
        renderSlot(EnchantingSkill::class, 'N')

        layoutSlot('X', viewIcon(ViewIconType.CROSS, ViewIconColor.RED) {
            displayName {
                primary("Schliessen".toSmallCaps())
            }
        }).onItemClick {
            closeForPlayer()
        }
    }
}