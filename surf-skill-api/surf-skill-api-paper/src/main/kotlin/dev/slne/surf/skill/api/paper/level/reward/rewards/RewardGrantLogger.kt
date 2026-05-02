package dev.slne.surf.skill.api.paper.level.reward.rewards

import dev.slne.surf.api.core.util.logger
import dev.slne.surf.skill.api.paper.SkillInstance
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.StandardOpenOption.APPEND
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.WRITE
import java.time.Instant
import kotlin.io.path.div

object RewardGrantLogger {
    private val log = logger()
    private val writeMutex = Mutex()
    private val logPath by lazy {
        JavaPlugin.getProvidingPlugin(RewardGrantLogger::class.java).dataFolder.toPath() / "reward-grants.log"
    }

    fun createAuxProtectHookIfAvailable() {
        if (Bukkit.getPluginManager().isPluginEnabled("AuxProtect")) {
            AuxProtectRewardGrantHook.create()
        }
    }

    internal fun log(player: Player, itemStack: ItemStack, delivery: RewardDelivery) {
        val line = buildLogLine(
            timestamp = Instant.now(),
            player = player,
            itemStack = itemStack,
            delivery = delivery,
            location = player.location
        )

        SkillInstance.launch(SkillInstance.asyncDispatcher) {
            writeLine(line)
        }

        if (Bukkit.getPluginManager().isPluginEnabled("AuxProtect")) {
            AuxProtectRewardGrantHook.log(player, itemStack, delivery)
        }
    }

    private suspend fun writeLine(line: String) {
        writeMutex.withLock {
            try {
                Files.createDirectories(logPath.parent)
                Files.writeString(
                    logPath,
                    line + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    CREATE,
                    WRITE,
                    APPEND
                )
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (exception: Exception) {
                log.atWarning()
                    .withCause(exception)
                    .log("Failed to write skill reward grant log entry")
            }
        }
    }

    private fun buildLogLine(
        timestamp: Instant,
        player: Player,
        itemStack: ItemStack,
        delivery: RewardDelivery,
        location: Location
    ): String {
        return listOf(
            "timestamp=${timestamp}",
            "player=${escape(player.name)}",
            "uuid=${player.uniqueId}",
            "rewardType=LevelItemRewards",
            "delivery=${delivery.name}",
            "item=${escape(itemStack.type.key.asString())}",
            "amount=${itemStack.amount}",
            "displayName=${escape(RewardGrantLogFormatter.displayName(itemStack))}",
            "enchantments=${escape(RewardGrantLogFormatter.formatEnchantments(itemStack))}",
            "world=${escape(location.world.name)}",
            "x=${RewardGrantLogFormatter.formatCoordinate(location.x)}",
            "y=${RewardGrantLogFormatter.formatCoordinate(location.y)}",
            "z=${RewardGrantLogFormatter.formatCoordinate(location.z)}",
            "yaw=${RewardGrantLogFormatter.formatCoordinate(location.yaw.toDouble())}",
            "pitch=${RewardGrantLogFormatter.formatCoordinate(location.pitch.toDouble())}"
        ).joinToString(" | ")
    }

    private fun escape(value: String) = RewardGrantLogFormatter.escapeTextLogValue(value)
}

internal enum class RewardDelivery {
    INVENTORY,
    DROPPED
}
