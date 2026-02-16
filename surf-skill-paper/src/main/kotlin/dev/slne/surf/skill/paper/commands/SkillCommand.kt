package dev.slne.surf.skill.paper.commands

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.skill.core.experience.ExperienceService
import dev.slne.surf.skill.paper.menu.SkillsView
import dev.slne.surf.skill.paper.plugin
import dev.slne.surf.skill.paper.utils.SkillPermissionRegistry
import dev.slne.surf.surfapi.bukkit.api.inventory.framework.viewFrame
import kotlinx.coroutines.withContext

fun skillCommand() = commandAPICommand("skill") {
    withPermission(SkillPermissionRegistry.COMMAND_SKILL)

    playerExecutor { player, arguments ->
        plugin.launch {
            val experiences = ExperienceService.getExperiencesForPlayer(player.uniqueId)

            withContext(plugin.globalRegionDispatcher) {
                viewFrame.open(
                    SkillsView::class.java,
                    player,
                    mapOf(
                        "skill_progress" to experiences,
                        "player_uuid" to player.uniqueId
                    )
                )
            }
        }
    }
}