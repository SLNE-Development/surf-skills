package dev.slne.surf.skill.core.paper.stats

import dev.slne.surf.api.core.util.logger
import dev.slne.surf.core.api.common.SurfCoreApi
import dev.slne.surf.skill.api.paper.SkillInstance
import dev.slne.surf.skill.api.paper.player.SkillPlayer
import dev.slne.surf.stats.api.SurfStatsApi
import dev.slne.surf.stats.api.model.PlayerStats
import dev.slne.surf.stats.api.model.StatEntry
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlin.jvm.Volatile
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object SkillStatsHook {
    private val log = logger()

    private val snapshots = ConcurrentHashMap<UUID, Map<String, Int>>()
    @Volatile
    private var flushJob: Job? = null

    suspend fun pushCurrent(player: SkillPlayer) {
        if (!hasStatsApi()) {
            return
        }
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
        if (!hasStatsApi()) {
            return
        }
        // Implemented in Task 6.
    }

    suspend fun flushDiff(player: SkillPlayer) {
        if (!hasStatsApi()) {
            return
        }
        // Implemented in Task 6.
    }

    suspend fun flushAllOnline() {
        if (!hasStatsApi()) {
            return
        }
        // Implemented in Task 7.
    }

    fun startPeriodicFlush() {
        if (!hasStatsApi()) {
            return
        }
        // Implemented in Task 7.
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
}
