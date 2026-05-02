package dev.slne.surf.skill.api.paper.level.reward.rewards

import dev.heliosares.auxprotect.AuxProtectPaper
import dev.heliosares.auxprotect.api.AuxProtectAPI
import dev.heliosares.auxprotect.database.DbEntry
import dev.heliosares.auxprotect.database.EntryAction
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.plugin.java.JavaPlugin

internal object AuxProtectRewardGrantHook {
    private val plugin by lazy {
        JavaPlugin.getProvidingPlugin(AuxProtectRewardGrantHook::class.java)
    }
    private val plainTextSerializer = PlainTextComponentSerializer.plainText()

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
                plugin.logger.warning("Failed to hook into AuxProtect: ${throwable.message}")
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
                plugin.logger.warning("Failed to write AuxProtect skill reward log entry: ${throwable.message}")
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

    private fun escapeDetailValue(value: Any?): String = value
        .toString()
        .replace("\\", "\\\\")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace(";", "\\;")
        .replace("=", "\\=")

    private fun buildDetails(
        location: Location,
        itemStack: ItemStack,
        delivery: RewardDelivery
    ) = listOf(
        "delivery=${escapeDetailValue(delivery.name)}",
        "item=${escapeDetailValue(itemStack.type.key.asString())}",
        "amount=${escapeDetailValue(itemStack.amount)}",
        "displayName=${escapeDetailValue(plainTextSerializer.serialize(itemStack.displayName()))}",
        "enchantments=${escapeDetailValue(formatEnchantments(itemStack))}",
        "world=${escapeDetailValue(location.world.name)}",
        "x=${escapeDetailValue(location.x)}",
        "y=${escapeDetailValue(location.y)}",
        "z=${escapeDetailValue(location.z)}",
        "yaw=${escapeDetailValue(location.yaw)}",
        "pitch=${escapeDetailValue(location.pitch)}"
    ).joinToString("; ")

    private fun formatEnchantments(itemStack: ItemStack): String {
        val enchantments = buildList {
            itemStack.enchantments.entries
                .sortedBy { it.key.key.asString() }
                .forEach { (enchantment, level) ->
                    add("${enchantment.key.asString()}:$level")
                }

            val storageMeta = itemStack.itemMeta as? EnchantmentStorageMeta ?: return@buildList
            storageMeta.storedEnchants.entries
                .sortedBy { it.key.key.asString() }
                .forEach { (enchantment, level) ->
                    add("stored:${enchantment.key.asString()}:$level")
                }
        }

        return enchantments.joinToString(",")
    }
}
