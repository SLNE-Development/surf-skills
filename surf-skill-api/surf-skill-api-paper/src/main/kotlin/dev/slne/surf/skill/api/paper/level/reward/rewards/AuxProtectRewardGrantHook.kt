package dev.slne.surf.skill.api.paper.level.reward.rewards

import dev.heliosares.auxprotect.AuxProtectPaper
import dev.heliosares.auxprotect.api.AuxProtectAPI
import dev.heliosares.auxprotect.database.DbEntry
import dev.heliosares.auxprotect.database.EntryAction
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

internal object AuxProtectRewardGrantHook {
    private val plugin by lazy {
        JavaPlugin.getProvidingPlugin(AuxProtectRewardGrantHook::class.java)
    }

    private lateinit var rewardInventoryAction: EntryAction
    private lateinit var rewardDroppedAction: EntryAction

    @Volatile
    private var created = false

    fun create() {
        Bukkit.getAsyncScheduler().runNow(plugin) {
            runCatching {
                createActions()
            }.onSuccess {
                plugin.logger.info("Hooked into AuxProtect. Skill item rewards will now be logged.")
            }.onFailure { throwable ->
                plugin.logger.log(Level.WARNING, "Failed to hook into AuxProtect", throwable)
            }
        }
    }

    fun log(player: Player, itemStack: ItemStack, delivery: RewardDelivery) {
        val label = AuxProtectPaper.getLabel(player)
        val target = itemStack.type.key.asString()
        val details = buildDetails(player.location, itemStack, delivery)

        Bukkit.getAsyncScheduler().runNow(plugin) {
            runCatching {
                createActions()
                AuxProtectAPI.add(
                    DbEntry(
                        label,
                        action(delivery),
                        true,
                        target,
                        details
                    )
                )
            }.onFailure { throwable ->
                plugin.logger.log(Level.WARNING, "Failed to write AuxProtect skill reward log entry", throwable)
            }
        }
    }

    private fun createActions() {
        if (created) return

        synchronized(this) {
            if (created) return

            rewardInventoryAction = createAction(
                plugin.name,
                "surfskills_reward_inventory",
                "received skill reward"
            )
            rewardDroppedAction = createAction(
                plugin.name,
                "surfskills_reward_dropped",
                "dropped skill reward"
            )
            created = true
        }
    }

    private fun createAction(name: String, key: String, nText: String): EntryAction = runCatching {
        AuxProtectAPI.createAction(name, key, nText, null)
    }.getOrNull() ?: EntryAction.getAction(key)

    private fun action(delivery: RewardDelivery) = when (delivery) {
        RewardDelivery.INVENTORY -> rewardInventoryAction
        RewardDelivery.DROPPED -> rewardDroppedAction
    }

    private fun buildDetails(
        location: Location,
        itemStack: ItemStack,
        delivery: RewardDelivery
    ) = listOf(
        "delivery=${escape(delivery.name)}",
        "item=${escape(itemStack.type.key.asString())}",
        "amount=${escape(itemStack.amount)}",
        "displayName=${escape(RewardGrantLogFormatter.displayName(itemStack))}",
        "enchantments=${escape(RewardGrantLogFormatter.formatEnchantments(itemStack))}",
        "world=${escape(location.world.name)}",
        "x=${escape(location.x)}",
        "y=${escape(location.y)}",
        "z=${escape(location.z)}",
        "yaw=${escape(location.yaw)}",
        "pitch=${escape(location.pitch)}"
    ).joinToString("; ")

    private fun escape(value: Any?) = RewardGrantLogFormatter.escapeAuxProtectDetailValue(value)
}
