# surf-stats Integration — Design

**Date:** 2026-05-02
**Status:** Approved

## Goal

Push per-player skill experience and level data from `surf-skills` to the `surf-stats` statistics platform, so the platform can display skill progression. The integration must be a **soft dependency**: `surf-skills` must continue to work normally if `surf-stats-paper` is not installed.

## Requirements

- Use the public `SurfStatsApi` from `dev.slne.surf.stats.api`.
- Persist both **current experience** and **current level** for each skill.
- Use **both** API paths:
  - `saveStats(uuid, stats)` — UPSERT current values; written from inside `SkillPlayerManager.savePlayer()`.
  - `saveDiffStats(uuid, diffs)` — append-only event log of XP gains; written from a periodic timer plus on quit and shutdown.
- Stats category: `surf:skills`. Stats keys: `surf:<skill.name>_experience` and `surf:<skill.name>_level`.
- Soft dependency only — no hard runtime coupling, no `NoClassDefFoundError` when `surf-stats-paper` is absent.

## Architecture

### Modules and dependencies

- `surf-skill-core-paper/build.gradle.kts`: add `compileOnlyApi("dev.slne.surf.stats:surf-stats-api:+")`. Mirrors the existing `surf-settings-api` setup.
- `surf-skill-paper/build.gradle.kts`: add `registerSoft("surf-stats-paper")` to `serverDependencies` (alongside the existing `surf-settings-paper` and `AuxProtect` soft entries).

### New package: `dev.slne.surf.skill.core.paper.stats` (in `surf-skill-core-paper`)

| File | Contents |
|---|---|
| `stats-util.kt` | `fun hasStatsApi() = pluginManager.isPluginEnabled("surf-stats-paper")` — analogous to `settings-util.kt`. |
| `SkillStatsKeys.kt` | Cached `Key` instances: `CATEGORY = key("surf", "skills")`, plus `xpKeyFor(skillName)` / `levelKeyFor(skillName)` returning `key("surf", "<name>_experience")` / `key("surf", "<name>_level")` from `ConcurrentHashMap` caches. |
| `SkillStatsHook.kt` | Object that owns the snapshot map and all push/flush logic. All public methods are no-ops when `!hasStatsApi()`. |

### `SkillStatsHook` — surface

```kotlin
private val snapshots = ConcurrentHashMap<UUID, Map<String, Int>>()
private var flushJob: Job? = null
```

| Method | Purpose |
|---|---|
| `suspend fun pushCurrent(player: SkillPlayer)` | Build a `PlayerStats` from all `SkillExperience`s and call `SurfStatsApi.saveStats(uuid, stats)`. Called from `savePlayer()`. |
| `fun seedSnapshot(player: SkillPlayer)` | Initialize `snapshots[uuid]` with the player's current XP per skill. Called after `fetchOrCreatePlayer` on join. |
| `suspend fun flushDiff(player: SkillPlayer)` | Compute `delta = current - snapshots[uuid]`, call `saveDiffStats` with only skills where `delta > 0`. Update the snapshot atomically. No-op when all deltas are 0. |
| `fun startPeriodicFlush()` | Launch a coroutine on `SkillInstance.asyncDispatcher` that runs `flushAllOnline` every 5 minutes. Stores the `Job` in `flushJob`. |
| `fun stopPeriodicFlush()` | `flushJob?.cancel()`. |
| `suspend fun flushAllOnline()` | For each online player, get the cached `SkillPlayer` and call `flushDiff`. |
| `fun dropSnapshot(uuid: UUID)` | Remove the player's snapshot entry after final flush. |

### Wiring

- `SkillPlayerManagerImpl.savePlayer(player)`: after the existing `ExperienceService.savePlayerExperience(...)` call, additionally call `SkillStatsHook.pushCurrent(player)`.
- `ExperienceServiceListener.onJoin`: after `SkillPlayerManager.fetchOrCreatePlayer(uuid)`, call `SkillStatsHook.seedSnapshot(player)`.
- `ExperienceServiceListener.onQuit`: call `SkillStatsHook.flushDiff(player)` **before** `SkillPlayerManager.savePlayer + invalidatePlayer`, then `SkillStatsHook.dropSnapshot(uuid)`.
- `PaperMain.onEnableAsync`: `SkillStatsHook.startPeriodicFlush()`.
- `PaperMain.onDisableAsync`: call `SkillStatsHook.stopPeriodicFlush()`, then for each online player call `flushDiff` **before** the existing `savePlayer` / `invalidatePlayer` loop, plus `dropSnapshot(uuid)`.

## Data flow

```
JOIN
  fetchOrCreatePlayer(uuid)       # loads XP from DB
  seedSnapshot(player)            # snapshots[uuid] = current XP
  → no saveStats, no saveDiffStats

XP-GAIN (during session)
  SkillPlayerImpl.incrementExperience(...)   # in-memory only
  → no surf-stats call

WORLDSAVE (all online players)
  savePlayer(uuid):
    savePlayerExperience(...)
    pushCurrent(player)           # saveStats UPSERT
  → diff snapshot untouched

PERIODIC TIMER (every 5 min)
  flushAllOnline:
    for each online player:
      flushDiff(player)
        delta = current - snapshots[uuid]
        if any delta > 0: saveDiffStats(uuid, deltas)
        snapshots[uuid] = current

QUIT
  flushDiff(player)               # rescue session delta
  savePlayer(uuid)                # → pushCurrent inside
  invalidatePlayer(uuid)
  dropSnapshot(uuid)

SHUTDOWN (PaperMain.onDisableAsync)
  stopPeriodicFlush()
  for each online player:
    flushDiff(player)
    savePlayer(uuid)              # → pushCurrent inside
    invalidatePlayer(uuid)
    dropSnapshot(uuid)
```

**Critical ordering on quit/shutdown:** `flushDiff` must run **before** `invalidatePlayer`. Otherwise `getPlayerIfCached(uuid)` is `null` and the snapshot can no longer be resolved.

**`pushCurrent` inside `savePlayer` runs after `savePlayerExperience`** so a `surf-stats` failure cannot block skill persistence.

## Mapping

For each `SkillExperience`, two `StatEntry`s are produced:

```kotlin
StatEntry(category = CATEGORY, key = xpKeyFor(skill.name),    value = currentExperience.toLong())
StatEntry(category = CATEGORY, key = levelKeyFor(skill.name), value = currentLevel.toLong())
```

`Skill.name` is already lowercase in the codebase (`"combat"`, `"mining"`, `"alchemy"`, ...).

`PlayerStats` is built with `serverName = SurfCoreApi.getCurrentServerName()`. `SurfCoreApi` is transitively available via `surf-api-core` — no new dependency needed.

`saveDiffStats` no longer takes a `clanUuid` — clan attribution is resolved internally by `surf-stats` via the `playerUuid`.

## Error handling and soft-dep behavior

**Class-loading gate:** All direct imports of `dev.slne.surf.stats.api.*` are confined to `SkillStatsHook` and `SkillStatsKeys`. Every public function on `SkillStatsHook` checks `hasStatsApi()` first. The JVM does not load `surf-stats` classes until the hook actually executes a body that touches them, so the absence of `surf-stats-paper` cannot trigger `NoClassDefFoundError`.

**Hook structure (illustrative):**

```kotlin
suspend fun pushCurrent(player: SkillPlayer) {
    if (!hasStatsApi()) {
        return
    }
    runCatching { doPushCurrent(player) }
        .onFailure { exception ->
            if (exception is CancellationException) {
                throw exception
            }
            log.atWarning()
                .withCause(exception)
                .log("Failed to push surf-stats current for ${player.uuid}")
        }
}

private suspend fun doPushCurrent(player: SkillPlayer) {
    SurfStatsApi.saveStats(player.uuid, buildPlayerStats(player))
}
```

`flushDiff` follows the same pattern. No `surf-stats` failure may propagate into `SkillPlayerManager.savePlayer` or the player lifecycle.

**Coroutines and cancellation:** `CancellationException` is explicitly re-thrown inside every `runCatching` block (consistent with the existing `RewardGrantLogger.writeLine` pattern). The periodic flush job uses `SkillInstance.launch(SkillInstance.asyncDispatcher)` and ends cleanly on `flushJob?.cancel()`.

**Concurrency:** `snapshots` is a `ConcurrentHashMap`. `flushDiff` performs an atomic read-modify-write via `snapshots.compute(uuid) { _, old -> current }` so two concurrent flushes (e.g. periodic timer firing while a quit is mid-flight) cannot ship the same delta twice.

**Empty-diff filter:** When all skill deltas are `<= 0`, `saveDiffStats` is **not** called — the snapshot is still updated. This avoids polluting `player_stats_history` with empty events.

**Code style:** All `if`/`else`/`when` bodies use braces, even single-statement ones (per project convention).

## Verification

There is no existing test infrastructure in the `surf-skills` repo (no `src/test`, no `*Test.kt`). Standing up a test framework solely for this hook is out of scope.

**Testable seam:** The diff math is extracted as a pure function with no Bukkit or coroutine dependencies, so it can be unit-tested if a test module is added later:

```kotlin
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
```

**Manual verification checklist (server boot with both plugins):**

1. **surf-stats absent (soft-dep proof):** Boot the server without `surf-stats-paper`. `surf-skills` must load. Join, gain XP, quit, world-save, shutdown — no `NoClassDefFoundError`, no `ServiceNotFoundException`, no surf-stats stack traces.
2. **surf-stats present, join:** Player joins. No new rows in `player_stats` for category `surf:skills`. No new rows in `player_stats_history`.
3. **surf-stats present, XP-gain + world-save:** Player gains XP, world-save fires. `player_stats` shows UPSERTed rows for `(uuid, surf:skills, surf:<skill>_experience)` and `..._level` with the current values. `player_stats_history` still empty for this category.
4. **surf-stats present, periodic diff (wait 5 min):** `player_stats_history` contains exactly the skills where `delta > 0` with correct delta values.
5. **surf-stats present, quit:** Last diff before quit lands in `player_stats_history`. Last UPSERT lands in `player_stats`.
6. **surf-stats present, shutdown:** All online players get a final diff + UPSERT.
7. **Empty-diff filter:** Player joins and quits without gaining XP → no new `player_stats_history` row.

## Out of scope

- Backfill of existing player skill data into `surf-stats`.
- Clan attribution from inside `surf-skills` (handled internally by `surf-stats`).
- Test infrastructure for `surf-skills` (no tests exist anywhere in the repo today).
- Dashboard / frontend changes on the `surf-stats` platform side.
