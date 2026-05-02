package dev.slne.surf.skill.core.paper.stats

import dev.slne.surf.api.core.util.logger
import dev.slne.surf.api.paper.extensions.server
import dev.slne.surf.core.api.common.SurfCoreApi
import dev.slne.surf.skill.api.paper.SkillInstance
import dev.slne.surf.skill.api.paper.player.SkillPlayer
import dev.slne.surf.skill.api.paper.player.SkillPlayerManager
import dev.slne.surf.stats.api.SurfStatsApi
import dev.slne.surf.stats.api.model.PlayerStats
import dev.slne.surf.stats.api.model.StatEntry
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.minutes

object SkillStatsHook {
    private val log = logger()

    private val snapshots = ConcurrentHashMap<UUID, Map<String, Int>>()

    @Volatile
    private var flushJob: Job? = null

    suspend fun pushCurrent(player: SkillPlayer) {
        try {
            SurfStatsApi.saveStats(player.uuid, buildPlayerStats(player))
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (exception: Exception) {
            log.atWarning()
                .withCause(exception)
                .log("Failed to push surf-stats current for ${player.uuid}")
        }
    }

    fun seedSnapshot(player: SkillPlayer) {
        snapshots[player.uuid] = snapshotOf(player)
    }

    suspend fun flushDiff(player: SkillPlayer) {
        val current = snapshotOf(player)
        var deltas: Map<String, Int> = emptyMap()
        snapshots.compute(player.uuid) { _, previous ->
            deltas = computeDeltas(current, previous ?: emptyMap())
            current
        }
        if (deltas.isEmpty()) {
            return
        }
        try {
            SurfStatsApi.saveDiffStats(player.uuid, buildDiffStats(player, deltas))
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (exception: Exception) {
            log.atWarning()
                .withCause(exception)
                .log("Failed to push surf-stats diff for ${player.uuid}")
        }
    }

    suspend fun flushAllOnline() {
        for (online in server.onlinePlayers) {
            val cached = SkillPlayerManager.getPlayerIfCached(online.uniqueId) ?: continue
            flushDiff(cached)
        }
    }

    fun startPeriodicFlush() {
        if (flushJob?.isActive == true) {
            return
        }
        flushJob = SkillInstance.launch(SkillInstance.asyncDispatcher) {
            while (isActive) {
                delay(5.minutes)
                try {
                    flushAllOnline()
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (exception: Exception) {
                    log.atWarning()
                        .withCause(exception)
                        .log("Periodic surf-stats diff flush failed")
                }
            }
        }
    }

    fun stopPeriodicFlush() {
        flushJob?.cancel()
        flushJob = null
    }

    fun dropSnapshot(uuid: UUID) {
        snapshots.remove(uuid)
    }

    internal fun computeDeltas(
        current: Map<String, Int>,
        previous: Map<String, Int>
    ): Map<String, Int> {
        return current.mapValues { (skill, currentXp) ->
            currentXp - (previous[skill] ?: 0)
        }.filterValues { delta ->
            delta > 0
        }
    }

    private fun snapshotOf(player: SkillPlayer): Map<String, Int> {
        return player.experiences.associate { experience ->
            experience.skill.name to experience.currentExperience
        }
    }

    private fun buildPlayerStats(player: SkillPlayer): PlayerStats {
        val entries = mutableListOf<StatEntry>()
        for (experience in player.experiences) {
            val skillName = experience.skill.name
            entries.add(
                StatEntry(
                    category = SkillStatsKeys.CATEGORY,
                    key = SkillStatsKeys.xpKeyFor(skillName),
                    value = experience.currentExperience.toLong()
                )
            )
            entries.add(
                StatEntry(
                    category = SkillStatsKeys.CATEGORY,
                    key = SkillStatsKeys.levelKeyFor(skillName),
                    value = experience.currentLevel.toLong()
                )
            )
        }
        return PlayerStats(
            playerUuid = player.uuid,
            serverName = SurfCoreApi.getCurrentServerName(),
            stats = entries
        )
    }

    private fun buildDiffStats(player: SkillPlayer, deltas: Map<String, Int>): PlayerStats {
        val entries = mutableListOf<StatEntry>()
        for ((skillName, deltaXp) in deltas) {
            val experience = player.experiences.firstOrNull { exp ->
                exp.skill.name == skillName
            } ?: continue
            entries.add(
                StatEntry(
                    category = SkillStatsKeys.CATEGORY,
                    key = SkillStatsKeys.xpKeyFor(skillName),
                    value = deltaXp.toLong()
                )
            )
            entries.add(
                StatEntry(
                    category = SkillStatsKeys.CATEGORY,
                    key = SkillStatsKeys.levelKeyFor(skillName),
                    value = experience.currentLevel.toLong()
                )
            )
        }
        return PlayerStats(
            playerUuid = player.uuid,
            serverName = SurfCoreApi.getCurrentServerName(),
            stats = entries
        )
    }
}
