package dev.slne.surf.skill.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.api.core.util.logger
import dev.slne.surf.api.paper.extensions.server
import dev.slne.surf.skill.api.paper.player.SkillPlayerManager
import dev.slne.surf.skill.core.paper.stats.StatsHook
import dev.slne.surf.skill.core.paper.stats.hasStatsApi
import dev.slne.surf.skill.paper.plugin
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.minutes

object StatsDiffSaveListener {
    private val log = logger()
    private val SAVE_INTERVAL = 5.minutes

    private val jobMutex = Mutex()
    private var job: Job? = null

    suspend fun start() {
        jobMutex.withLock {
            job?.cancelAndJoin()
            job = plugin.launch {
                delay(SAVE_INTERVAL)
                while (isActive) {
                    runCatching { saveDiffsForOnlinePlayers() }
                        .onFailure { ex ->
                            log.atSevere()
                                .withCause(ex)
                                .log("Failed periodic skill stats diff save")
                        }
                    delay(SAVE_INTERVAL)
                }
            }
        }
    }

    suspend fun stop() {
        jobMutex.withLock {
            job?.cancelAndJoin()
            job = null
        }
    }

    private suspend fun saveDiffsForOnlinePlayers() {
        if (!hasStatsApi()) {
            return
        }

        val players = server.onlinePlayers.mapNotNull { onlinePlayer ->
            SkillPlayerManager.getPlayerIfCached(onlinePlayer.uniqueId)
        }

        coroutineScope {
            players.forEach { skillPlayer ->
                launch {
                    runCatching {
                        StatsHook.saveDiff(skillPlayer.uuid, skillPlayer.experiences)
                    }.onFailure { ex ->
                        log.atWarning()
                            .withCause(ex)
                            .log("Failed to push skill diff stats for ${skillPlayer.uuid}")
                    }
                }
            }
        }
    }
}
