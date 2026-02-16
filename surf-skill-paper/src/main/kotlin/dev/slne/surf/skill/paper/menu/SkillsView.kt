package dev.slne.surf.skill.paper.menu

import dev.slne.surf.skill.api.Skill
import dev.slne.surf.skill.api.progress.SkillProgress
import dev.slne.surf.skill.api.skills.*
import dev.slne.surf.skill.paper.menu.utils.outlineItem
import dev.slne.surf.surfapi.bukkit.api.inventory.framework.titleBuilder
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import it.unimi.dsi.fastutil.objects.ObjectList
import me.devnatan.inventoryframework.View
import me.devnatan.inventoryframework.ViewConfigBuilder
import me.devnatan.inventoryframework.context.RenderContext
import me.devnatan.inventoryframework.context.SlotClickContext
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.inventory.ItemStack

class SkillsView : View() {
    private val skillProgressState =
        initialState<ObjectList<SkillProgress>>("skill_progress")

    override fun onInit(config: ViewConfigBuilder) {
        config
            .titleBuilder {
                primary("Skills".toSmallCaps(), TextDecoration.BOLD)
            }
            .size(5)
            .layout(
                "OOOOOOOOO",
                "OMOCOFOAO",
                "OOOOOOOOO",
                "OWOEOIONO",
                "OOOOOOOOO",
            )
            .cancelInteractions()
    }

    private inline fun <reified S : Skill> buildSkillItem(context: RenderContext): Pair<SkillProgress, ItemStack> {
        val skillProgresses = skillProgressState.get(context)
        val skillProgressWithSkill = skillProgresses.first { it.skill is S }

        return skillProgressWithSkill to skillProgressWithSkill.skill.displayItemStack(
            skillProgressWithSkill
        )
    }

    private fun skillClickAction(progress: SkillProgress, event: SlotClickContext) {
        event.openForPlayer(SkillView::class.java, mapOf("skill_progress" to progress))
    }

    private inline fun <reified S : Skill> renderSlot(context: RenderContext, slot: Char) {
        val skillItem = buildSkillItem<S>(context)

        context.layoutSlot(slot, skillItem.second)
            .onClick { event -> skillClickAction(skillItem.first, event) }
    }

    override fun onFirstRender(render: RenderContext) {
        render.layoutSlot('O', outlineItem)

        // Skills
        renderSlot<MiningSkill>(render, 'M')
        renderSlot<CombatSkill>(render, 'C')
        renderSlot<ForagingSkill>(render, 'F')
        renderSlot<AlchemySkill>(render, 'A')

        renderSlot<WoodcuttingSkill>(render, 'W')
        renderSlot<ExplorationSkill>(render, 'E')
        renderSlot<FishingSkill>(render, 'I')
        renderSlot<EnchantingSkill>(render, 'N')
    }
}