package dev.slne.surf.skill.core.paper.stats

import dev.slne.surf.api.core.util.logger
import dev.slne.surf.skill.api.paper.SkillInstance
import dev.slne.surf.skill.api.paper.player.SkillPlayer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object SkillStatsHook {
    private val log = logger()

    private val snapshots = ConcurrentHashMap<UUID, Map<String, Int>>()
    private var flushJob: Job? = null

    suspend fun pushCurrent(player: SkillPlayer) {
        if (!hasStatsApi()) {
            return
        }
        // Implemented in Task 5.
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
}
