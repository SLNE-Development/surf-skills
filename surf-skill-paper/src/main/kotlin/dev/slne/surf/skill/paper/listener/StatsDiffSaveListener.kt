package dev.slne.surf.skill.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.api.core.util.logger
import dev.slne.surf.api.paper.extensions.server
import dev.slne.surf.skill.api.paper.player.SkillPlayerManager
import dev.slne.surf.skill.core.paper.stats.StatsHook
import dev.slne.surf.skill.paper.plugin
import kotlinx.coroutines.*
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
                while (isActive) {
                    runCatching { saveDiffsForOnlinePlayers() }
                    delay(SAVE_INTERVAL)
                }
            }
        }
    }

    suspend fun stop() {
        jobMutex.withLock {
            job?.cancel()
            job = null
        }
    }

    private suspend fun saveDiffsForOnlinePlayers() {
        val players = server.onlinePlayers.mapNotNull {
            SkillPlayerManager.getPlayerIfCached(it.uniqueId)
        }

        supervisorScope {
            players.forEach { skillPlayer ->
                launch {
                    if (!isActive) return@launch

                    runCatching {
                        StatsHook.saveDiff(skillPlayer.uuid, skillPlayer.experiences)
                    }
                }
            }
        }
    }
}
