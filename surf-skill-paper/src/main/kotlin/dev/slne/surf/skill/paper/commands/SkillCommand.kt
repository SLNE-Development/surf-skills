package dev.slne.surf.skill.paper.commands

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.skill.api.player.SkillPlayerManager
import dev.slne.surf.skill.paper.menu.skillsView
import dev.slne.surf.skill.paper.plugin
import dev.slne.surf.skill.paper.utils.SkillPermissionRegistry
import dev.slne.surf.api.paper.inventory.framework.open
import kotlinx.coroutines.withContext

fun skillCommand() = commandAPICommand("skill") {
    withPermission(SkillPermissionRegistry.COMMAND_SKILL)

    playerExecutor { player, arguments ->
        plugin.launch {
            val skillPlayer = SkillPlayerManager.fetchOrCreatePlayer(player.uniqueId)

            withContext(plugin.globalRegionDispatcher) {
                skillsView.open(
                    player, mapOf(
                        "skill_progress" to skillPlayer.experiences,
                        "player_uuid" to player.uniqueId
                    )
                )
            }
        }
    }
}