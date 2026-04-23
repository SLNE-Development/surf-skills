package dev.slne.surf.skill.paper.commands

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.arguments.AsyncPlayerProfileArgument
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.paper.command.executors.playerExecutorSuspend
import dev.slne.surf.api.paper.command.util.awaitAsyncPlayerProfile
import dev.slne.surf.api.paper.command.util.idOrThrow
import dev.slne.surf.api.paper.inventory.framework.open
import dev.slne.surf.skill.api.paper.player.SkillPlayerManager
import dev.slne.surf.skill.paper.commands.argument.skillArgument
import dev.slne.surf.skill.paper.menu.skillsView
import dev.slne.surf.skill.paper.plugin
import dev.slne.surf.skill.paper.utils.SkillPermissionRegistry
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit

fun skillCommand() = commandTree("skill") {
    withPermission(SkillPermissionRegistry.COMMAND_SKILL)

    playerExecutor { player, _ ->
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

    literalArgument("stats") {
        withPermission(SkillPermissionRegistry.COMMAND_SKILL_STATS)
        anyExecutor { sender, arguments ->
            val topTenOverallByExperience = TODO()

            sender.sendText {

            }
        }

        skillArgument("skill") {
            anyExecutor { sender, arguments ->

            }
        }
    }

    argument(AsyncPlayerProfileArgument("target")) {
        withPermission(SkillPermissionRegistry.COMMAND_SKILL_OTHER)
        playerExecutorSuspend { player, arguments ->
            val target =
                Bukkit.getOfflinePlayer(arguments.awaitAsyncPlayerProfile("target").idOrThrow())

            if (!target.hasPlayedBefore()) {
                player.sendText {
                    appendErrorPrefix()
                    error("Der Spieler hat noch nie auf diesem Server gespielt.")
                }
                return@playerExecutorSuspend
            }

            val skillPlayer = SkillPlayerManager.fetchOrCreatePlayer(target.uniqueId)

            withContext(plugin.globalRegionDispatcher) {
                skillsView.open(
                    player, mapOf(
                        "skill_progress" to skillPlayer.experiences,
                        "player_uuid" to target.uniqueId
                    )
                )
            }
        }
    }
}