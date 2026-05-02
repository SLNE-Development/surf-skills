package dev.slne.surf.skill.core.paper.stats

import dev.slne.surf.api.core.messages.adventure.key
import dev.slne.surf.core.api.common.SurfCoreApi
import dev.slne.surf.skill.api.paper.experience.SkillExperience
import dev.slne.surf.stats.api.SurfStatsApi
import dev.slne.surf.stats.api.model.PlayerStats
import dev.slne.surf.stats.api.model.StatEntry
import it.unimi.dsi.fastutil.objects.ObjectList
import java.util.UUID

object StatsHook {
    private val CATEGORY = key("surf:skills")

    suspend fun saveCurrent(uuid: UUID, experiences: ObjectList<SkillExperience>) {
        if (!hasStatsApi()) {
            return
        }
        SurfStatsApi.saveStats(uuid, buildPlayerStats(uuid, experiences))
    }

    suspend fun saveDiff(uuid: UUID, experiences: ObjectList<SkillExperience>) {
        if (!hasStatsApi()) {
            return
        }
        SurfStatsApi.saveDiffStats(uuid, buildPlayerStats(uuid, experiences))
    }

    private fun buildPlayerStats(
        uuid: UUID,
        experiences: ObjectList<SkillExperience>
    ): PlayerStats {
        val entries = experiences.flatMap { exp ->
            val skillName = exp.skill.name
            listOf(
                StatEntry(CATEGORY, key("surf:${skillName}_level"), exp.currentLevel.toLong()),
                StatEntry(CATEGORY, key("surf:${skillName}_experience"), exp.currentExperience.toLong()),
            )
        }
        return PlayerStats(uuid, SurfCoreApi.getCurrentServerName(), entries)
    }
}
