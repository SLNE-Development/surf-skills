@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.paper.menu

import dev.slne.surf.skill.api.Skill
import dev.slne.surf.skill.api.experience.SkillExperience
import dev.slne.surf.skill.api.level.LevelState
import dev.slne.surf.skill.api.level.SkillLevel
import dev.slne.surf.skill.paper.menu.utils.MenuHeads
import dev.slne.surf.skill.paper.menu.utils.outlineItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.inventory.framework.modifyConfig
import dev.slne.surf.surfapi.bukkit.api.inventory.framework.titleBuilder
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import io.papermc.paper.datacomponent.DataComponentTypes
import me.devnatan.inventoryframework.View
import me.devnatan.inventoryframework.ViewConfigBuilder
import me.devnatan.inventoryframework.component.Pagination
import me.devnatan.inventoryframework.context.OpenContext
import me.devnatan.inventoryframework.context.RenderContext
import me.devnatan.inventoryframework.state.State
import org.bukkit.inventory.ItemType

class SkillView : View() {
    private val skillExperienceState = initialState<SkillExperience>("skill_progress")

    private val paginationState: State<Pagination> =
        buildLazyPaginationState<SkillLevel> { context ->
            val state = skillExperienceState.get(context)

            state.skill.getLevels()
        }.elementFactory { context, builder, _, level ->
            val state = skillExperienceState.get(context)
            val levelState = state.checkLevel(level.level)

            builder.withItem(buildLevelItem(state, level, levelState)).onClick { event ->
                // OPEN LEVEL INFO PAGE
            }
        }.layoutTarget('L').build()

    private fun buildLevelItem(
        progress: SkillExperience,
        level: SkillLevel,
        state: LevelState
    ) = buildItem(state.itemType, level.level) {
        displayName {
            primary("Level ${level.level}".toSmallCaps())
        }

        lore(level.buildLore(progress))

        setData(DataComponentTypes.MAX_STACK_SIZE, Skill.MAX_SKILL_LEVEL)
    }

    private val backItem = MenuHeads.CROSS.clone().apply {
        displayName {
            primary("Zurück".toSmallCaps())
        }
    }

    private val previousItem = MenuHeads.ARROW_LEFT.clone().apply {
        displayName {
            primary("Vorherige Seite".toSmallCaps())
        }
    }

    private val nextItem = MenuHeads.ARROW_RIGHT.clone().apply {
        displayName {
            primary("Nächste Seite".toSmallCaps())
        }
    }

    private val loadingItem = buildItem(ItemType.CLOCK) {
        displayName {
            primary("Lade...".toSmallCaps())
        }
    }

    override fun onInit(config: ViewConfigBuilder) {
        config
            .title("")
            .layout(
                "OOOOOOOOO",
                "OOLLLLLOO",
                "OOLLLLLOO",
                "OOOOOOOOO",
                "OOOOBOOOO"
            )
            .cancelInteractions()
    }

    override fun onFirstRender(render: RenderContext) {
        val pagination = paginationState.get(render)

        render.slot(2, 4, loadingItem)
            .displayIf(pagination::isLoading)
            .updateOnStateChange(paginationState)

        render.layoutSlot('O', outlineItem)

        val inventorySize = render.inventory.size
        val rows = inventorySize / 9

        render.slot(rows, 4, previousItem)
            .updateOnStateChange(paginationState)
            .displayIf(pagination::canBack)
            .onClick(pagination::back)

        render.layoutSlot('B', backItem)
            .onClick { event -> event.back() }

        render.slot(rows, 6, nextItem)
            .updateOnStateChange(paginationState)
            .displayIf(pagination::canAdvance)
            .onClick(pagination::advance)
    }

    override fun onOpen(open: OpenContext) {
        val skill = skillExperienceState.get(open).skill

        open.modifyConfig {
            titleBuilder {
                primary("Skill".toSmallCaps())
                appendSpace()
                append(skill.displayName)
            }
        }
    }
}