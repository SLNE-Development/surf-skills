package dev.slne.surf.skill.paper.commands

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.arguments.AsyncPlayerProfileArgument
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.paper.command.executors.anyExecutorSuspend
import dev.slne.surf.api.paper.command.executors.playerExecutorSuspend
import dev.slne.surf.api.paper.command.util.awaitAsyncPlayerProfile
import dev.slne.surf.api.paper.command.util.idOrThrow
import dev.slne.surf.api.paper.inventory.framework.open
import dev.slne.surf.skill.api.paper.Skill
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

    literalArgument("#admin") {
        withPermission(SkillPermissionRegistry.COMMAND_SKILL_ADMIN)
        argument(AsyncPlayerProfileArgument("target")) {
            skillArgument("skill") {
                literalArgument("setLevel") {
                    integerArgument("level") {
                        anyExecutorSuspend { sender, arguments ->
                            val target = Bukkit.getOfflinePlayer(
                                arguments.awaitAsyncPlayerProfile("target").idOrThrow()
                            )
                            val skill: Skill by arguments
                            val level: Int by arguments

                            if (!target.hasPlayedBefore()) {
                                sender.sendText {
                                    appendErrorPrefix()
                                    error("Der Spieler hat noch nie auf diesem Server gespielt.")
                                }
                                return@anyExecutorSuspend
                            }

                            val skillPlayer =
                                SkillPlayerManager.fetchOrCreatePlayer(target.uniqueId)

                            skillPlayer.incrementExperience(
                                skill::class,
                                skill.experienceCurve.getTotalExperienceForLevel(level) - skillPlayer.experiences.filter { it.skill == skill }
                                    .sumOf { it.currentExperience })

                            sender.sendText {
                                appendSuccessPrefix()
                                success("Die Stufe von ")
                                append(skill.displayName)
                                success(" für ")
                                variableValue(target.name ?: target.uniqueId.toString())
                                success(" wurde auf ")
                                variableValue(level)
                                success(" gesetzt.")
                            }
                        }
                    }
                }

                literalArgument("setBeforeLevel") {
                    integerArgument("level") {
                        anyExecutorSuspend { sender, arguments ->
                            val target = Bukkit.getOfflinePlayer(
                                arguments.awaitAsyncPlayerProfile("target").idOrThrow()
                            )
                            val skill: Skill by arguments
                            val level: Int by arguments

                            if (!target.hasPlayedBefore()) {
                                sender.sendText {
                                    appendErrorPrefix()
                                    error("Der Spieler hat noch nie auf diesem Server gespielt.")
                                }
                                return@anyExecutorSuspend
                            }

                            val skillPlayer =
                                SkillPlayerManager.fetchOrCreatePlayer(target.uniqueId)

                            skillPlayer.incrementExperience(
                                skill::class,
                                skill.experienceCurve.getTotalExperienceForLevel(level) - 1 - skillPlayer.experiences.filter { it.skill == skill }
                                    .sumOf { it.currentExperience })

                            sender.sendText {
                                appendSuccessPrefix()
                                success("Die Stufe von ")
                                append(skill.displayName)
                                success(" für ")
                                variableValue(target.name ?: target.uniqueId.toString())
                                success(" wurde auf ")
                                variableValue(level - 1)
                                success(" gesetzt.")
                            }
                        }
                    }
                }

                literalArgument("setExperience") {
                    integerArgument("experience") {
                        anyExecutorSuspend { sender, arguments ->
                            val target = Bukkit.getOfflinePlayer(
                                arguments.awaitAsyncPlayerProfile("target").idOrThrow()
                            )
                            val skill: Skill by arguments
                            val experience: Int by arguments

                            if (!target.hasPlayedBefore()) {
                                sender.sendText {
                                    appendErrorPrefix()
                                    error("Der Spieler hat noch nie auf diesem Server gespielt.")
                                }
                                return@anyExecutorSuspend
                            }

                            val skillPlayer =
                                SkillPlayerManager.fetchOrCreatePlayer(target.uniqueId)

                            skillPlayer.incrementExperience(
                                skill::class,
                                experience - skillPlayer.experiences.filter { it.skill == skill }
                                    .sumOf { it.currentExperience })

                            sender.sendText {
                                appendSuccessPrefix()
                                success("Die Erfahrung von ")
                                append(skill.displayName)
                                success(" für ")
                                variableValue(target.name ?: target.uniqueId.toString())
                                success(" wurde auf ")
                                variableValue(experience)
                                success(" gesetzt.")
                            }
                        }
                    }
                }
            }
        }
    }
}