package dev.slne.surf.skill.core.paper.stats

import dev.slne.surf.api.core.messages.adventure.key
import net.kyori.adventure.key.Key
import java.util.concurrent.ConcurrentHashMap

internal object SkillStatsKeys {
    val CATEGORY: Key = key("surf:skills")

    private val xpKeys = ConcurrentHashMap<String, Key>()
    private val levelKeys = ConcurrentHashMap<String, Key>()

    fun xpKeyFor(skillName: String): Key {
        return xpKeys.computeIfAbsent(skillName) { name ->
            key("surf:${name}_experience")
        }
    }

    fun levelKeyFor(skillName: String): Key {
        return levelKeys.computeIfAbsent(skillName) { name ->
            key("surf:${name}_level")
        }
    }
}
