# surf-stats Integration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Push surf-skills experience and level data to surf-stats as a soft dependency, with `saveStats` UPSERT on every player save and `saveDiffStats` event log via a 5-minute periodic timer plus quit/shutdown flushes.

**Architecture:** New `dev.slne.surf.skill.core.paper.stats` package in `surf-skill-core-paper` with three files (`stats-util.kt`, `SkillStatsKeys.kt`, `SkillStatsHook.kt`). Wiring into `SkillPlayerManagerImpl.savePlayer`, `ExperienceServiceListener` (join/quit), and `PaperMain` (enable/disable). The hook is fully gated by `pluginManager.isPluginEnabled("surf-stats-paper")`, mirroring the existing `SettingsHook` pattern.

**Tech Stack:** Kotlin, Paper API, kotlinx-coroutines, Caffeine cache (already in deps), surf-stats-api (compileOnly).

**Reference:** Spec at `docs/superpowers/specs/2026-05-02-surf-stats-integration-design.md`.

**Build commands (Windows PowerShell, working dir `C:/Development/Projects/surf-skills`):**
- Compile core-paper: `.\gradlew :surf-skill-core:surf-skill-core-paper:compileKotlin`
- Compile paper plugin: `.\gradlew :surf-skill-paper:compileKotlin`
- Full build: `.\gradlew build`

**Commit style:** Match recent commits, e.g. `feat(stats): ...`, `chore(stats): ...`.

**Code style note:** All `if`/`else`/`when` bodies use braces. No inline single-statement ifs.

---

## File Structure

| File | Action | Purpose |
|---|---|---|
| `surf-skill-core-paper/build.gradle.kts` | Modify | Add `compileOnlyApi` for `surf-stats-api`. |
| `surf-skill-paper/build.gradle.kts` | Modify | Add `registerSoft("surf-stats-paper")`. |
| `surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/stats/stats-util.kt` | Create | `hasStatsApi()` helper. |
| `surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/stats/SkillStatsKeys.kt` | Create | Cached `Key` builders for category and stat keys. |
| `surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/stats/SkillStatsHook.kt` | Create | Snapshot map, push/flush logic, periodic timer. |
| `surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/player/SkillPlayerManagerImpl.kt` | Modify | Call `pushCurrent` after `savePlayerExperience`. |
| `surf-skill-paper/src/main/kotlin/dev/slne/surf/skill/paper/listener/ExperienceServiceListener.kt` | Modify | Seed snapshot on join, flush diff on quit, drop snapshot. |
| `surf-skill-paper/src/main/kotlin/dev/slne/surf/skill/paper/PaperMain.kt` | Modify | Start/stop periodic flush, flush online players on disable. |

---

## Task 1: Add Gradle Dependencies

**Files:**
- Modify: `surf-skill-core-paper/build.gradle.kts`
- Modify: `surf-skill-paper/build.gradle.kts`

- [ ] **Step 1: Add `compileOnlyApi` to core-paper**

Open `surf-skill-core/surf-skill-core-paper/build.gradle.kts`. After the existing `compileOnlyApi("dev.slne.surf.settings:surf-settings-api:+")` line in `dependencies { ... }`, add a new line:

```kotlin
compileOnlyApi("dev.slne.surf.stats:surf-stats-api:+")
```

The full `dependencies` block should look like:

```kotlin
dependencies {
    api(projects.surfSkillCore.surfSkillCoreCommon)
    api(projects.surfSkillApi.surfSkillApiPaper)
    compileOnly(libs.surf.enchantment.api)
    compileOnlyApi("dev.slne.surf.settings:surf-settings-api:+")
    compileOnlyApi("dev.slne.surf.stats:surf-stats-api:+")
}
```

- [ ] **Step 2: Add `registerSoft("surf-stats-paper")` to paper plugin**

Open `surf-skill-paper/build.gradle.kts`. Inside the `serverDependencies { ... }` block, after the existing `registerSoft("AuxProtect")` line, add:

```kotlin
registerSoft("surf-stats-paper")
```

The full `serverDependencies` block should be:

```kotlin
serverDependencies {
    registerRequired(
        "surf-enchantment-paper",
        joinClassPath = true,
        loadOrder = PaperPluginDescription.RelativeLoadOrder.BEFORE
    )
    registerRequired("surf-rabbitmq-paper")
    registerSoft("surf-settings-paper")
    registerSoft("AuxProtect")
    registerSoft("surf-stats-paper")
}
```

- [ ] **Step 3: Verify the dependency resolves**

Run: `.\gradlew :surf-skill-core:surf-skill-core-paper:dependencies --configuration compileClasspath` (PowerShell)
Expected: output contains `dev.slne.surf.stats:surf-stats-api` somewhere in the tree without `FAILED`.

- [ ] **Step 4: Commit**

```powershell
git add surf-skill-core/surf-skill-core-paper/build.gradle.kts surf-skill-paper/build.gradle.kts
git commit -m "chore(stats): add surf-stats-api compileOnly dep and soft plugin entry"
```

---

## Task 2: Create `stats-util.kt`

**Files:**
- Create: `surf-skill-core/surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/stats/stats-util.kt`

- [ ] **Step 1: Create the file with the `hasStatsApi` helper**

Mirrors `surf-skill-core/surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/settings/settings-util.kt` exactly.

```kotlin
package dev.slne.surf.skill.core.paper.stats

import dev.slne.surf.api.paper.extensions.pluginManager

fun hasStatsApi() = pluginManager.isPluginEnabled("surf-stats-paper")
```

- [ ] **Step 2: Compile core-paper**

Run: `.\gradlew :surf-skill-core:surf-skill-core-paper:compileKotlin`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Commit**

```powershell
git add surf-skill-core/surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/stats/stats-util.kt
git commit -m "feat(stats): add hasStatsApi soft-dep gate"
```

---

## Task 3: Create `SkillStatsKeys.kt`

**Files:**
- Create: `surf-skill-core/surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/stats/SkillStatsKeys.kt`

- [ ] **Step 1: Create the file**

```kotlin
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
```

- [ ] **Step 2: Compile core-paper**

Run: `.\gradlew :surf-skill-core:surf-skill-core-paper:compileKotlin`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Commit**

```powershell
git add surf-skill-core/surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/stats/SkillStatsKeys.kt
git commit -m "feat(stats): add cached stat key builders for surf:skills category"
```

---

## Task 4: Create `SkillStatsHook` Skeleton with State and Pure Diff Function

**Files:**
- Create: `surf-skill-core/surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/stats/SkillStatsHook.kt`

- [ ] **Step 1: Create the skeleton file**

This step creates the object with state, logger, the pure `computeDeltas` function, and stub bodies for the other public methods. Subsequent tasks will fill in the bodies.

```kotlin
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
```

- [ ] **Step 2: Compile core-paper**

Run: `.\gradlew :surf-skill-core:surf-skill-core-paper:compileKotlin`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Commit**

```powershell
git add surf-skill-core/surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/stats/SkillStatsHook.kt
git commit -m "feat(stats): add SkillStatsHook skeleton with snapshot map and diff math"
```

---

## Task 5: Implement `pushCurrent` (saveStats UPSERT)

**Files:**
- Modify: `surf-skill-core/surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/stats/SkillStatsHook.kt`

- [ ] **Step 1: Add imports**

At the top of `SkillStatsHook.kt`, add the following imports (alongside the existing ones):

```kotlin
import dev.slne.surf.core.api.common.SurfCoreApi
import dev.slne.surf.stats.api.SurfStatsApi
import dev.slne.surf.stats.api.model.PlayerStats
import dev.slne.surf.stats.api.model.StatEntry
```

- [ ] **Step 2: Add `buildPlayerStats` private helper**

Inside the `SkillStatsHook` object, just below the `snapshotOf` helper, add:

```kotlin
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
```

- [ ] **Step 3: Replace `pushCurrent` body**

Replace the stub body of `pushCurrent` (the `// Implemented in Task 5.` comment) with:

```kotlin
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
```

- [ ] **Step 4: Compile core-paper**

Run: `.\gradlew :surf-skill-core:surf-skill-core-paper:compileKotlin`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit**

```powershell
git add surf-skill-core/surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/stats/SkillStatsHook.kt
git commit -m "feat(stats): implement pushCurrent for saveStats UPSERT"
```

---

## Task 6: Implement `seedSnapshot` and `flushDiff` (saveDiffStats)

**Files:**
- Modify: `surf-skill-core/surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/stats/SkillStatsHook.kt`

- [ ] **Step 1: Replace `seedSnapshot` body**

Replace the stub body of `seedSnapshot` with:

```kotlin
fun seedSnapshot(player: SkillPlayer) {
    if (!hasStatsApi()) {
        return
    }
    snapshots[player.uuid] = snapshotOf(player)
}
```

- [ ] **Step 2: Add `buildDiffStats` private helper**

Inside the object, just below `buildPlayerStats`, add:

```kotlin
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
```

Note: For diff entries we ship the XP **delta** (gained since last snapshot) and the **current** level. The level is not a delta — it is the new absolute level at flush time, which is what the time-series consumer needs.

- [ ] **Step 3: Replace `flushDiff` body**

Replace the stub body of `flushDiff` with:

```kotlin
suspend fun flushDiff(player: SkillPlayer) {
    if (!hasStatsApi()) {
        return
    }
    val current = snapshotOf(player)
    val deltas = mutableMapOf<String, Int>()
    snapshots.compute(player.uuid) { _, previous ->
        val computed = computeDeltas(current, previous ?: emptyMap())
        deltas.putAll(computed)
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
```

- [ ] **Step 4: Compile core-paper**

Run: `.\gradlew :surf-skill-core:surf-skill-core-paper:compileKotlin`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit**

```powershell
git add surf-skill-core/surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/stats/SkillStatsHook.kt
git commit -m "feat(stats): implement seedSnapshot and flushDiff with atomic snapshot update"
```

---

## Task 7: Implement Periodic Flush Timer

**Files:**
- Modify: `surf-skill-core/surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/stats/SkillStatsHook.kt`

- [ ] **Step 1: Add imports**

At the top of `SkillStatsHook.kt`, add the following imports:

```kotlin
import dev.slne.surf.api.paper.extensions.server
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration.Companion.minutes
```

- [ ] **Step 2: Replace `flushAllOnline` body**

Replace the stub body of `flushAllOnline` with:

```kotlin
suspend fun flushAllOnline() {
    if (!hasStatsApi()) {
        return
    }
    val playerManager = dev.slne.surf.skill.api.paper.player.SkillPlayerManager
    for (online in server.onlinePlayers) {
        val cached = playerManager.getPlayerIfCached(online.uniqueId) ?: continue
        flushDiff(cached)
    }
}
```

- [ ] **Step 3: Replace `startPeriodicFlush` body**

Replace the stub body of `startPeriodicFlush` with:

```kotlin
fun startPeriodicFlush() {
    if (!hasStatsApi()) {
        return
    }
    if (flushJob?.isActive == true) {
        return
    }
    flushJob = SkillInstance.launch(SkillInstance.asyncDispatcher) {
        while (coroutineContext.isActive) {
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
```

- [ ] **Step 4: Compile core-paper**

Run: `.\gradlew :surf-skill-core:surf-skill-core-paper:compileKotlin`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit**

```powershell
git add surf-skill-core/surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/stats/SkillStatsHook.kt
git commit -m "feat(stats): add 5-minute periodic diff flush timer"
```

---

## Task 8: Wire `pushCurrent` into `SkillPlayerManagerImpl.savePlayer`

**Files:**
- Modify: `surf-skill-core/surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/player/SkillPlayerManagerImpl.kt`

- [ ] **Step 1: Add import**

At the top of `SkillPlayerManagerImpl.kt`, add:

```kotlin
import dev.slne.surf.skill.core.paper.stats.SkillStatsHook
```

- [ ] **Step 2: Update `savePlayer(player)` to call `pushCurrent` after `savePlayerExperience`**

Replace the existing `savePlayer(player)` method:

```kotlin
override suspend fun savePlayer(player: SkillPlayer) {
    ExperienceService.savePlayerExperience(player.uuid, player.experiences)
}
```

with:

```kotlin
override suspend fun savePlayer(player: SkillPlayer) {
    ExperienceService.savePlayerExperience(player.uuid, player.experiences)
    SkillStatsHook.pushCurrent(player)
}
```

- [ ] **Step 3: Compile core-paper**

Run: `.\gradlew :surf-skill-core:surf-skill-core-paper:compileKotlin`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 4: Commit**

```powershell
git add surf-skill-core/surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/player/SkillPlayerManagerImpl.kt
git commit -m "feat(stats): hook saveStats into SkillPlayerManagerImpl.savePlayer"
```

---

## Task 9: Wire snapshot lifecycle into `ExperienceServiceListener`

**Files:**
- Modify: `surf-skill-paper/src/main/kotlin/dev/slne/surf/skill/paper/listener/ExperienceServiceListener.kt`

- [ ] **Step 1: Add import**

At the top of `ExperienceServiceListener.kt`, add:

```kotlin
import dev.slne.surf.skill.core.paper.stats.SkillStatsHook
```

- [ ] **Step 2: Replace `onJoin` to seed the snapshot after fetch**

Replace the existing `onJoin` handler:

```kotlin
@EventHandler
fun onJoin(event: PlayerJoinEvent) {
    val player = event.player
    val uuid = player.uniqueId

    plugin.launch {
        SkillPlayerManager.fetchOrCreatePlayer(uuid)
    }
}
```

with:

```kotlin
@EventHandler
fun onJoin(event: PlayerJoinEvent) {
    val player = event.player
    val uuid = player.uniqueId

    plugin.launch {
        val skillPlayer = SkillPlayerManager.fetchOrCreatePlayer(uuid)
        SkillStatsHook.seedSnapshot(skillPlayer)
    }
}
```

- [ ] **Step 3: Replace `onQuit` to flush diff before invalidate, then drop snapshot**

Replace the existing `onQuit` handler:

```kotlin
@EventHandler
fun onQuit(event: PlayerQuitEvent) {
    val player = event.player
    val uuid = player.uniqueId

    plugin.launch {
        SkillPlayerManager.savePlayer(uuid)
        SkillPlayerManager.invalidatePlayer(uuid)
    }
}
```

with:

```kotlin
@EventHandler
fun onQuit(event: PlayerQuitEvent) {
    val player = event.player
    val uuid = player.uniqueId

    plugin.launch {
        val cached = SkillPlayerManager.getPlayerIfCached(uuid)
        if (cached != null) {
            SkillStatsHook.flushDiff(cached)
        }
        SkillPlayerManager.savePlayer(uuid)
        SkillPlayerManager.invalidatePlayer(uuid)
        SkillStatsHook.dropSnapshot(uuid)
    }
}
```

- [ ] **Step 4: Compile paper plugin**

Run: `.\gradlew :surf-skill-paper:compileKotlin`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit**

```powershell
git add surf-skill-paper/src/main/kotlin/dev/slne/surf/skill/paper/listener/ExperienceServiceListener.kt
git commit -m "feat(stats): seed and flush stat snapshots on player join/quit"
```

---

## Task 10: Wire periodic flush into `PaperMain`

**Files:**
- Modify: `surf-skill-paper/src/main/kotlin/dev/slne/surf/skill/paper/PaperMain.kt`

- [ ] **Step 1: Add imports**

At the top of `PaperMain.kt`, add:

```kotlin
import dev.slne.surf.skill.core.paper.stats.SkillStatsHook
```

- [ ] **Step 2: Start the periodic flush in `onEnableAsync`**

In `onEnableAsync`, after the existing `skillCommand()` line, add `SkillStatsHook.startPeriodicFlush()`. The method becomes:

```kotlin
override suspend fun onEnableAsync() {
    skillManagerImpl.registerAllSkills()
    ListenerManager.register()
    RewardGrantLogger.createAuxProtectHookIfAvailable()

    if (hasSettingsApi()) {
        SettingsHook.registerSettings()
    }

    skillCommand()
    SkillStatsHook.startPeriodicFlush()
}
```

- [ ] **Step 3: Replace `onDisableAsync` to stop the timer and flush diffs before save**

Replace the existing `onDisableAsync`:

```kotlin
override suspend fun onDisableAsync() {
    server.onlinePlayers.forEach { player ->
        val uuid = player.uniqueId

        SkillPlayerManager.savePlayer(uuid)
        SkillPlayerManager.invalidatePlayer(uuid)
    }

    PaperSkillInstance.paperLoader.onDisable()
}
```

with:

```kotlin
override suspend fun onDisableAsync() {
    SkillStatsHook.stopPeriodicFlush()

    server.onlinePlayers.forEach { player ->
        val uuid = player.uniqueId

        val cached = SkillPlayerManager.getPlayerIfCached(uuid)
        if (cached != null) {
            SkillStatsHook.flushDiff(cached)
        }
        SkillPlayerManager.savePlayer(uuid)
        SkillPlayerManager.invalidatePlayer(uuid)
        SkillStatsHook.dropSnapshot(uuid)
    }

    PaperSkillInstance.paperLoader.onDisable()
}
```

Note: `server` is already imported in `PaperMain.kt` (used for `server.onlinePlayers`). No new `server` import needed.

- [ ] **Step 4: Compile paper plugin**

Run: `.\gradlew :surf-skill-paper:compileKotlin`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit**

```powershell
git add surf-skill-paper/src/main/kotlin/dev/slne/surf/skill/paper/PaperMain.kt
git commit -m "feat(stats): start periodic flush on enable, final flush on disable"
```

---

## Task 11: Full Build Verification

**Files:** none.

- [ ] **Step 1: Full build**

Run: `.\gradlew build`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 2: Verify generated paper-plugin.yml soft-dep entry**

Check that the plugin descriptor lists `surf-stats-paper` as a soft dependency. After build, run:

```powershell
Select-String -Path "surf-skill-paper\build\resources\main\paper-plugin.yml" -Pattern "surf-stats-paper"
```

Expected: at least one line matching `surf-stats-paper` under a `dependencies:` or `softdepend:` block.

- [ ] **Step 3: Sanity-check the stats package**

Run:

```powershell
Get-ChildItem -Path "surf-skill-core\surf-skill-core-paper\src\main\kotlin\dev\slne\surf\skill\core\paper\stats"
```

Expected: three files — `stats-util.kt`, `SkillStatsKeys.kt`, `SkillStatsHook.kt`.

- [ ] **Step 4: No-op commit only if needed**

If `git status` shows clean, skip. If any whitespace/CRLF artifacts surfaced during the build (e.g. generated descriptor changes that were committed accidentally — they shouldn't be), do **not** commit them; investigate first.

---

## Manual Verification (post-merge, on a Paper server)

These steps require a running Paper 1.21+ server with `surf-rabbitmq-paper` and the surf-stats microservice + database. They are NOT part of the plan execution but document how the feature is verified end-to-end. See spec §Verification for full list.

1. Boot **without** `surf-stats-paper` jar — surf-skills must load and operate normally.
2. Boot **with** both plugins — join a player, gain XP from a tracked skill, wait for a world-save, and confirm a row appears in `player_stats` for `(uuid, surf:skills, surf:<skill>_experience)` and `..._level`.
3. Wait 5 minutes — `player_stats_history` gets a row with the XP delta gained in that window.
4. Quit the player — final diff lands in `player_stats_history` immediately.
5. Restart server with the player offline at boot — no surf-stats activity for that player.
