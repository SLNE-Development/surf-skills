# surf-stats Integration in surf-skills — Design

**Date:** 2026-05-02
**Status:** Approved (pending implementation)
**Scope:** Add `surf-stats` as a soft dependency to `surf-skills`. Persist per-player skill level and experience to surf-stats via `saveStats` (current snapshot) and `saveDiffStats` (periodic deltas).

## Context

`surf-stats` is a Paper plugin + microservice pair that captures per-player statistics and persists them via RabbitMQ. It exposes a Bukkit service `SurfStatsApi` with two relevant calls:

- `saveStats(playerUuid, PlayerStats)` — UPSERTs current absolute values keyed by `(player_uuid, category, key, server_name)`.
- `saveDiffStats(playerUuid, PlayerStats)` — accepts **absolute current values**; the microservice computes `delta = entry.value - last_diff_value` against the per-tuple baseline and appends a `player_stats_history` row when `delta > 0`.

`surf-skills` already maintains per-player `SkillExperience` (with `currentExperience: Int` and `currentLevel: Int`) and saves it through its own RabbitMQ pipeline (`ExperienceService`) on player quit, world save, and plugin disable. We want to mirror this state into surf-stats so that skill level and experience become queryable as time-series stats alongside Minecraft's native statistics.

## Goals

1. Push current absolute skill level + experience to surf-stats whenever surf-skills already saves player data.
2. Push periodic diffs (every 5 minutes) so that XP and level gains over time are queryable.
3. Make the integration a strict soft dependency — surf-skills must continue to work if surf-stats is missing or broken.
4. Be threadsafe under Folia / coroutine concurrency.

## Non-Goals

- Configurability of the save interval.
- Opt-out integration (handled transparently by the surf-stats microservice via `player_stat_optouts`).
- Test infrastructure (surf-skills has no test suite today; out of scope).

## Data Model

**Category:** `surf:skills` (single namespaced key, identical for every entry written by surf-skills).

**Keys per skill:** Two entries per `Skill`, where `<skill>` is `Skill.name` (e.g., `combat`, `mining`):

| Key | Value | Source |
|---|---|---|
| `surf:<skill>_level` | `SkillExperience.currentLevel.toLong()` | per-skill current level |
| `surf:<skill>_experience` | `SkillExperience.currentExperience.toLong()` | per-skill total absolute XP |

Both are written via `saveStats` (current snapshot) **and** via `saveDiffStats` (periodic). Level diffs are sparse but meaningful — they represent "this player gained N levels in this 5-minute window."

**Server name:** `SurfCoreApi.getCurrentServerName()`.

## Architecture

Split between `surf-skill-core-paper` (integration logic) and `surf-skill-paper` (lifecycle), matching the existing `SettingsHook` / `RewardGrantLogger` / `AuxProtect` pattern.

```
┌────────────────────────────────────────────┐         ┌──────────────────────┐
│ surf-skill-core-paper                      │         │ surf-skill-paper     │
│                                            │         │                      │
│  StatsHook (object)                        │         │  StatsDiffSaveListener│
│  ├─ hasStatsApi()                          │         │  ├─ start()          │
│  ├─ saveCurrent(uuid, experiences)         │ ◄────── │  └─ stop()           │
│  └─ saveDiff(uuid, experiences)            │ called  │     (5-min loop)     │
│                                            │  by     │                      │
│  SkillPlayerManagerImpl.savePlayer(player) │         │  PaperMain           │
│  └─ ExperienceService.savePlayerExperience │         │  ├─ onEnable: start()│
│  └─ StatsHook.saveCurrent (runCatching)    │         │  └─ onDisable: stop()│
└────────────────────────────────────────────┘         └──────────────────────┘
                              │
                              ▼ SurfStatsApi (soft dep)
                    ┌─────────────────────┐
                    │ surf-stats-paper    │
                    │ → microservice      │
                    │ → DB                │
                    └─────────────────────┘
```

## Components

### `StatsHook` (new — `surf-skill-core-paper/.../stats/StatsHook.kt`)

Stateless `object`. Converts `SkillExperience → PlayerStats` and forwards to `SurfStatsApi`. No coroutine/job state.

```kotlin
object StatsHook {
    private val CATEGORY = key("surf:skills")

    suspend fun saveCurrent(uuid: UUID, experiences: ObjectList<SkillExperience>) {
        if (!hasStatsApi()) return
        SurfStatsApi.saveStats(uuid, buildPlayerStats(uuid, experiences))
    }

    suspend fun saveDiff(uuid: UUID, experiences: ObjectList<SkillExperience>) {
        if (!hasStatsApi()) return
        SurfStatsApi.saveDiffStats(uuid, buildPlayerStats(uuid, experiences))
    }

    private fun buildPlayerStats(uuid: UUID, experiences: ObjectList<SkillExperience>): PlayerStats {
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
```

### `stats-util.kt` (new — `surf-skill-core-paper/.../stats/stats-util.kt`)

Top-level helper, analogous to `settings-util.kt`:

```kotlin
fun hasStatsApi() = pluginManager.isPluginEnabled("surf-stats-paper")
```

### `SkillPlayerManagerImpl.savePlayer` (modified)

After the existing `ExperienceService.savePlayerExperience(...)`, call `StatsHook.saveCurrent(...)` wrapped in `runCatching`. surf-skills' own persistence must not fail if surf-stats is broken.

```kotlin
override suspend fun savePlayer(player: SkillPlayer) {
    ExperienceService.savePlayerExperience(player.uuid, player.experiences)

    runCatching {
        StatsHook.saveCurrent(player.uuid, player.experiences)
    }.onFailure { ex ->
        log.atWarning().withCause(ex).log("Failed to push current skill stats to surf-stats")
    }
}
```

This single hook covers all three save triggers (player quit, world save, plugin disable) because all paths funnel through `savePlayer(player)`.

### `StatsDiffSaveListener` (new — `surf-skill-paper/.../listener/StatsDiffSaveListener.kt`)

Periodic 5-minute loop. Owns its own `Job`, guarded by a `Mutex` so `start`/`stop` are race-free.

```kotlin
object StatsDiffSaveListener {
    private val log = logger()
    private val SAVE_INTERVAL = 5.minutes

    private val jobMutex = Mutex()
    private var job: Job? = null

    fun start() {
        plugin.launch {
            jobMutex.withLock {
                job?.cancel()
                job = plugin.launch {
                    delay(SAVE_INTERVAL)
                    while (isActive) {
                        runCatching { saveDiffsForOnlinePlayers() }
                            .onFailure { log.atSevere().withCause(it).log("Failed periodic skill stats diff save") }
                        delay(SAVE_INTERVAL)
                    }
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
        if (!StatsHook.hasStatsApi()) return

        val players = server.onlinePlayers.mapNotNull {
            SkillPlayerManager.getPlayerIfCached(it.uniqueId)
        }

        coroutineScope {
            players.forEach { player ->
                launch {
                    runCatching {
                        StatsHook.saveDiff(player.uuid, player.experiences)
                    }.onFailure { ex ->
                        log.atWarning().withCause(ex)
                            .log("Failed to push skill diff stats for ${player.uuid}")
                    }
                }
            }
        }
    }
}
```

Only currently-online players are diff-saved. Offline cached players have no XP changes, so the delta would always be 0 — wasted round-trips.

### `PaperMain` (modified)

```kotlin
override suspend fun onEnableAsync() {
    skillManagerImpl.registerAllSkills()
    ListenerManager.register()
    RewardGrantLogger.createAuxProtectHookIfAvailable()

    if (hasSettingsApi()) {
        SettingsHook.registerSettings()
    }

    StatsDiffSaveListener.start()

    skillCommand()
}

override suspend fun onDisableAsync() {
    StatsDiffSaveListener.stop()  // before final saves so the periodic loop doesn't race them

    server.onlinePlayers.forEach { player ->
        val uuid = player.uniqueId
        SkillPlayerManager.savePlayer(uuid)
        SkillPlayerManager.invalidatePlayer(uuid)
    }

    PaperSkillInstance.paperLoader.onDisable()
}
```

## Build Configuration

### `surf-skill-core/surf-skill-core-paper/build.gradle.kts`

Add:
```kotlin
compileOnlyApi("dev.slne.surf.stats:surf-stats-api:+")
compileOnlyApi("dev.slne.surf.core:surf-core-api-common:+")
```

`surf-core-api-common` is **not** transitive via `surf-api-core` and is required for `SurfCoreApi.getCurrentServerName()`.

### `surf-skill-paper/build.gradle.kts`

Add to `serverDependencies`:
```kotlin
registerSoft("surf-stats-paper")
```

The reposilite repository is already configured globally via `settings.gradle.kts` `pluginManagement`.

## Threadsafety

| Concern | Mitigation |
|---|---|
| `StatsHook` shared state | None — `object` is stateless; converts on each call |
| `experiences` iteration during convert | `SkillPlayer.experiences` is `.freeze()`'d (`SkillPlayerImpl.kt:29`) — immutable |
| `pluginManager.isPluginEnabled()` | Bukkit guarantees thread-safety |
| Concurrent `start()`/`stop()` of periodic job | `kotlinx.coroutines.sync.Mutex` guards `job` reference |
| In-flight save during plugin disable | `cancelAndJoin()` waits for current batch |
| Periodic loop racing final-save during disable | `stop()` runs **before** the final `savePlayer` loop in `onDisableAsync` |
| surf-skills' RabbitMQ disconnect mid-save | Order in `onDisableAsync`: `stop()` → final-save loop (uses surf-skills RabbitMQ via `ExperienceService`) → `paperLoader.onDisable()` (disconnects). All saves complete before disconnect. |
| Live `server.onlinePlayers` mutation during iteration | Snapshot copy (`mapNotNull { ... }`) before `forEach` |
| `getPlayerIfCached` thread-safety | Backed by `ConcurrentHashMap` (`SkillPlayerManagerImpl.kt:15`) |
| One slow player save blocking others | Per-player `launch` inside `coroutineScope` parallelizes; `runCatching` isolates failures |
| Concurrent diff-saves for same player | Idempotent: `saveDiffStats` with the same absolute value computes `delta = 0` → no history row written |

## Error Handling

1. **Soft-dep missing or disabled:** `hasStatsApi()` gates all `SurfStatsApi` calls. Class loading is lazy — `SurfStatsApi.kt:7`'s `requiredService<SurfStatsApi>()` is only triggered when `StatsHook` first touches it, which happens only after `hasStatsApi()` returns true.
2. **surf-stats microservice down (RabbitMQ timeout):** `runCatching` in `savePlayer` and the periodic loop catches and logs. surf-skills' own DB save runs **before** the surf-stats call — its persistence is unaffected.
3. **`SurfCoreApi.getCurrentServerName()` not registered:** Should not happen at runtime (transitive via surf-api-paper). Any failure is caught by the same `runCatching`.
4. **Initial diff-save (baseline = 0):** Per the documented surf-stats contract (`README.md:107`), the first call writes the absolute value as the first delta. This matches our XP semantics (XP starts at 0, so the first absolute is the first real gain).

## Files

### New

1. `surf-skill-core/surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/stats/StatsHook.kt`
2. `surf-skill-core/surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/stats/stats-util.kt`
3. `surf-skill-paper/src/main/kotlin/dev/slne/surf/skill/paper/listener/StatsDiffSaveListener.kt`

### Modified

1. `surf-skill-core/surf-skill-core-paper/build.gradle.kts`
2. `surf-skill-paper/build.gradle.kts`
3. `surf-skill-core/surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/player/SkillPlayerManagerImpl.kt`
4. `surf-skill-paper/src/main/kotlin/dev/slne/surf/skill/paper/PaperMain.kt`

## Implementation Order

1. Build configuration — Gradle deps + soft-dep registration
2. `stats-util.kt` (`hasStatsApi()`)
3. `StatsHook` with conversion + wrappers
4. `SkillPlayerManagerImpl.savePlayer` patch — current-save flow now end-to-end
5. `StatsDiffSaveListener`
6. `PaperMain` wiring — start/stop in lifecycle

Steps 1–4 are a logically-complete unit (current snapshot end-to-end). Steps 5–6 add periodic diffs.
