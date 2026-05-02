# surf-stats Integration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add `surf-stats` as a soft dependency to surf-skills and persist per-player skill level + experience to surf-stats via `saveStats` (snapshot, on each existing save trigger) and `saveDiffStats` (5-minute periodic).

**Architecture:** Stateless `StatsHook` object in `surf-skill-core-paper` performs the data conversion (`SkillExperience → PlayerStats`) and forwards to `SurfStatsApi`. A `StatsDiffSaveListener` object in `surf-skill-paper` owns the periodic 5-minute coroutine. Lifecycle is wired in `PaperMain`. Soft-dep is gated by `pluginManager.isPluginEnabled("surf-stats-paper")`; if missing, every call is a no-op.

**Tech Stack:** Kotlin, mccoroutine-folia, Paper API, `surf-stats-api`, `surf-core-api-common`, Gradle (Kotlin DSL).

---

## Spec Reference

`docs/superpowers/specs/2026-05-02-surf-stats-integration-design.md`

## Coding Conventions

- **Always wrap `if`/`else`/`when` bodies in braces.** No inline single-statement ifs (e.g., `if (x) return` is forbidden — use `if (x) { return }`).
- Match existing import/package style (kebab-case file names like `stats-util.kt` are valid Kotlin).
- Use `private val log = logger()` (from `dev.slne.surf.api.core.util.logger`) for class-level loggers, matching `RewardGrantLogger` pattern.

## Verification Strategy

surf-skills has **no test suite**. Per-task verification is:
1. **Compile check** — `./gradlew :<module>:compileKotlin` must succeed.
2. **Final task only** — `./gradlew build` produces the plugin jar at `surf-skill-paper/build/libs/`.

If a compile step fails, fix the issue inline (do not commit a broken state).

---

### Task 1: Add Gradle dependencies and soft-dep registration

**Files:**
- Modify: `surf-skill-core/surf-skill-core-paper/build.gradle.kts`
- Modify: `surf-skill-paper/build.gradle.kts`

- [ ] **Step 1: Add `compileOnlyApi` deps to core-paper**

Open `surf-skill-core/surf-skill-core-paper/build.gradle.kts`. Replace the entire `dependencies { ... }` block with:

```kotlin
dependencies {
    api(projects.surfSkillCore.surfSkillCoreCommon)
    api(projects.surfSkillApi.surfSkillApiPaper)
    compileOnly(libs.surf.enchantment.api)
    compileOnlyApi("dev.slne.surf.settings:surf-settings-api:+")
    compileOnlyApi("dev.slne.surf.stats:surf-stats-api:+")
    compileOnlyApi("dev.slne.surf.core:surf-core-api-common:+")
}
```

- [ ] **Step 2: Register soft-dep in paper module**

Open `surf-skill-paper/build.gradle.kts`. Inside `serverDependencies { ... }` (after `registerSoft("AuxProtect")`), add one line:

```kotlin
registerSoft("surf-stats-paper")
```

The full `serverDependencies` block should now read:

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

- [ ] **Step 3: Verify compile**

Run: `./gradlew :surf-skill-core:surf-skill-core-paper:compileKotlin :surf-skill-paper:processResources`
Expected: `BUILD SUCCESSFUL`. If reposilite cannot resolve `surf-stats-api` or `surf-core-api-common`, double-check the artifact coordinates match what's published at `https://reposilite.slne.dev`.

- [ ] **Step 4: Commit**

```bash
git add surf-skill-core/surf-skill-core-paper/build.gradle.kts surf-skill-paper/build.gradle.kts
git commit -m "build: add surf-stats and surf-core-api-common deps with soft-dep registration"
```

---

### Task 2: Add `stats-util.kt` with `hasStatsApi()`

**Files:**
- Create: `surf-skill-core/surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/stats/stats-util.kt`

- [ ] **Step 1: Create the file**

Create `surf-skill-core/surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/stats/stats-util.kt` with this exact content:

```kotlin
package dev.slne.surf.skill.core.paper.stats

import dev.slne.surf.api.paper.extensions.pluginManager

fun hasStatsApi() = pluginManager.isPluginEnabled("surf-stats-paper")
```

- [ ] **Step 2: Verify compile**

Run: `./gradlew :surf-skill-core:surf-skill-core-paper:compileKotlin`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Commit**

```bash
git add surf-skill-core/surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/stats/stats-util.kt
git commit -m "feat(stats): add hasStatsApi soft-dep gate"
```

---

### Task 3: Add `StatsHook` with conversion and save wrappers

**Files:**
- Create: `surf-skill-core/surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/stats/StatsHook.kt`

- [ ] **Step 1: Create the file**

Create `surf-skill-core/surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/stats/StatsHook.kt` with this exact content:

```kotlin
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
```

**Notes for the implementer:**
- `key("surf:skills")` comes from `dev.slne.surf.api.core.messages.adventure.key` — same factory used in surf-stats README example (line 116).
- `experiences` parameter is the same type/instance returned by `SkillPlayer.experiences`, which is `.freeze()`-immutable, so iteration is safe.
- Both `save*` functions are `suspend` because `SurfStatsApi.saveStats` and `saveDiffStats` are suspend.
- `hasStatsApi()` is the top-level helper from `stats-util.kt` (Task 2). It is in the same package, so no import is needed.

- [ ] **Step 2: Verify compile**

Run: `./gradlew :surf-skill-core:surf-skill-core-paper:compileKotlin`
Expected: `BUILD SUCCESSFUL`. If `key` cannot be resolved, verify the import path against `RewardGrantLogger` or check `surf-api-core` for the helper's actual location.

- [ ] **Step 3: Commit**

```bash
git add surf-skill-core/surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/stats/StatsHook.kt
git commit -m "feat(stats): add StatsHook for surf-stats save wrappers"
```

---

### Task 4: Hook `StatsHook.saveCurrent` into `SkillPlayerManagerImpl.savePlayer`

**Files:**
- Modify: `surf-skill-core/surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/player/SkillPlayerManagerImpl.kt`

- [ ] **Step 1: Add logger import and field**

Open `SkillPlayerManagerImpl.kt`. Add this import alongside existing imports (after `import com.sksamuel.aedile.core.asLoadingCache`):

```kotlin
import dev.slne.surf.api.core.util.logger
import dev.slne.surf.skill.core.paper.stats.StatsHook
```

Inside the class body, add the logger field as the first line (right after `class SkillPlayerManagerImpl : SkillPlayerManager, Services.Fallback {`):

```kotlin
    private val log = logger()
```

The class header should now read:

```kotlin
@AutoService(SkillPlayerManager::class)
class SkillPlayerManagerImpl : SkillPlayerManager, Services.Fallback {
    private val log = logger()
    private val syncCache = ConcurrentHashMap<UUID, SkillPlayer>()
    ...
```

- [ ] **Step 2: Modify `savePlayer(player)` to call `StatsHook.saveCurrent`**

Locate the existing `override suspend fun savePlayer(player: SkillPlayer)` method. Replace it entirely with:

```kotlin
    override suspend fun savePlayer(player: SkillPlayer) {
        ExperienceService.savePlayerExperience(player.uuid, player.experiences)

        runCatching {
            StatsHook.saveCurrent(player.uuid, player.experiences)
        }.onFailure { ex ->
            log.atWarning()
                .withCause(ex)
                .log("Failed to push current skill stats to surf-stats")
        }
    }
```

- [ ] **Step 3: Verify compile**

Run: `./gradlew :surf-skill-core:surf-skill-core-paper:compileKotlin`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 4: Commit**

```bash
git add surf-skill-core/surf-skill-core-paper/src/main/kotlin/dev/slne/surf/skill/core/paper/player/SkillPlayerManagerImpl.kt
git commit -m "feat(stats): mirror current skill stats to surf-stats on save"
```

---

### Task 5: Add `StatsDiffSaveListener` for periodic diff saves

**Files:**
- Create: `surf-skill-paper/src/main/kotlin/dev/slne/surf/skill/paper/listener/StatsDiffSaveListener.kt`

- [ ] **Step 1: Create the file**

Create `surf-skill-paper/src/main/kotlin/dev/slne/surf/skill/paper/listener/StatsDiffSaveListener.kt` with this exact content:

```kotlin
package dev.slne.surf.skill.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.api.core.util.logger
import dev.slne.surf.api.paper.extensions.server
import dev.slne.surf.skill.api.paper.player.SkillPlayerManager
import dev.slne.surf.skill.core.paper.stats.StatsHook
import dev.slne.surf.skill.core.paper.stats.hasStatsApi
import dev.slne.surf.skill.paper.plugin
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.minutes

object StatsDiffSaveListener {
    private val log = logger()
    private val SAVE_INTERVAL = 5.minutes

    private val jobMutex = Mutex()
    private var job: Job? = null

    suspend fun start() {
        jobMutex.withLock {
            job?.cancel()
            job = plugin.launch {
                delay(SAVE_INTERVAL)
                while (isActive) {
                    runCatching { saveDiffsForOnlinePlayers() }
                        .onFailure { ex ->
                            log.atSevere()
                                .withCause(ex)
                                .log("Failed periodic skill stats diff save")
                        }
                    delay(SAVE_INTERVAL)
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
        if (!hasStatsApi()) {
            return
        }

        val players = server.onlinePlayers.mapNotNull { onlinePlayer ->
            SkillPlayerManager.getPlayerIfCached(onlinePlayer.uniqueId)
        }

        coroutineScope {
            players.forEach { skillPlayer ->
                launch {
                    runCatching {
                        StatsHook.saveDiff(skillPlayer.uuid, skillPlayer.experiences)
                    }.onFailure { ex ->
                        log.atWarning()
                            .withCause(ex)
                            .log("Failed to push skill diff stats for ${skillPlayer.uuid}")
                    }
                }
            }
        }
    }
}
```

**Notes for the implementer:**
- `plugin.launch { ... }` (from `mccoroutine-folia`) returns a `Job` and uses the plugin's coroutine scope so the job is cancelled when the plugin is disabled.
- `Mutex` from `kotlinx.coroutines.sync` — guards the `job` reference against concurrent `start`/`stop`.
- The inner `launch` inside `coroutineScope` is from `kotlinx.coroutines.launch` (not the mccoroutine extension) — `coroutineScope` provides the `CoroutineScope` receiver. `import kotlinx.coroutines.launch` is the one to use here.
- `hasStatsApi()` is a top-level function (not a method on the `StatsHook` object), so it is imported separately and called bare. This matches the existing `hasSettingsApi()` pattern in `SkillPlayerImpl.kt:75`.

- [ ] **Step 2: Verify compile**

Run: `./gradlew :surf-skill-paper:compileKotlin`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Commit**

```bash
git add surf-skill-paper/src/main/kotlin/dev/slne/surf/skill/paper/listener/StatsDiffSaveListener.kt
git commit -m "feat(stats): add periodic 5-minute skill diff stats save"
```

---

### Task 6: Wire `StatsDiffSaveListener` into `PaperMain` lifecycle

**Files:**
- Modify: `surf-skill-paper/src/main/kotlin/dev/slne/surf/skill/paper/PaperMain.kt`

- [ ] **Step 1: Add import**

Open `PaperMain.kt`. Add this import alongside the existing `dev.slne.surf.skill.paper.listener.ListenerManager` import:

```kotlin
import dev.slne.surf.skill.paper.listener.StatsDiffSaveListener
```

- [ ] **Step 2: Start the periodic job in `onEnableAsync`**

Locate the existing `override suspend fun onEnableAsync()` method. Replace it entirely with:

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
```

- [ ] **Step 3: Stop the periodic job FIRST in `onDisableAsync`**

Locate the existing `override suspend fun onDisableAsync()` method. Replace it entirely with:

```kotlin
    override suspend fun onDisableAsync() {
        StatsDiffSaveListener.stop()

        server.onlinePlayers.forEach { player ->
            val uuid = player.uniqueId

            SkillPlayerManager.savePlayer(uuid)
            SkillPlayerManager.invalidatePlayer(uuid)
        }

        PaperSkillInstance.paperLoader.onDisable()
    }
```

The order is important: `stop()` first → no more diff-save coroutines running → final-save loop runs (which uses surf-skills' RabbitMQ via `ExperienceService`) → then `paperLoader.onDisable()` disconnects surf-skills' RabbitMQ.

- [ ] **Step 4: Verify compile**

Run: `./gradlew :surf-skill-paper:compileKotlin`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Run full build**

Run: `./gradlew build`
Expected: `BUILD SUCCESSFUL`. Plugin jar at `surf-skill-paper/build/libs/surf-skill-paper-<version>.jar`.

- [ ] **Step 6: Commit**

```bash
git add surf-skill-paper/src/main/kotlin/dev/slne/surf/skill/paper/PaperMain.kt
git commit -m "feat(stats): wire periodic diff save listener into plugin lifecycle"
```

---

## Self-Review Checklist (post-implementation, manual)

Before declaring done, verify:

1. ✅ **Soft-dep gating works** — Start the server **without** surf-stats-paper installed. surf-skills should enable cleanly, players should join/quit/save without errors. No `NoClassDefFoundError`. Look for the absence of `Failed to push current skill stats to surf-stats` warnings (if you see them, the gate is working but something else is calling).
2. ✅ **Soft-dep present, current save works** — Start with surf-stats-paper installed. Trigger a player save (quit). Check the `player_stats` table for entries with `category = 'surf:skills'` and keys like `surf:combat_level`, `surf:combat_experience`. Values should match `currentLevel` / `currentExperience`.
3. ✅ **Periodic diff save works** — Stay online for >5 min, gain XP, observe entries appearing in `player_stats_history` with category `surf:skills` and the same key shape. First entry should be the absolute value (baseline starts at 0); subsequent entries should be deltas.
4. ✅ **Disable order safe** — Trigger a plugin disable while the periodic loop is mid-iteration. No exceptions, all online players get final-saved.

If any check fails, file a follow-up issue rather than silently fixing — the design assumes these contracts hold and a regression is signal worth investigating.
