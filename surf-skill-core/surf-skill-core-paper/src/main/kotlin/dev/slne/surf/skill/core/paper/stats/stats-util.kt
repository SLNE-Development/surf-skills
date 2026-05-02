package dev.slne.surf.skill.core.paper.stats

import dev.slne.surf.api.paper.extensions.pluginManager

fun hasStatsApi() = pluginManager.isPluginEnabled("surf-stats-paper")
