package dev.slne.skill.core.database

import dev.slne.surf.surfapi.core.api.util.requiredService
import java.nio.file.Path

private val databaseLoader = requiredService<DatabaseLoader>()

interface DatabaseLoader {
    suspend fun connect(dataPath: Path)
    fun disconnect()

    companion object : DatabaseLoader by databaseLoader {
        val INSTANCE get() = databaseLoader
    }
}