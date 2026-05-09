package dev.slne.surf.skill.core.paper.stats

import dev.slne.surf.api.paper.extensions.pluginManager

val hasStatsApi by lazy {
    pluginManager.isPluginEnabled("surf-stats-paper")
}
