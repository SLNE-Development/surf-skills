package dev.slne.surf.skill.api.paper.level.reward.rewards

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import java.util.Locale

internal object RewardGrantLogFormatter {
    private val plainTextSerializer = PlainTextComponentSerializer.plainText()

    fun displayName(itemStack: ItemStack) = plainTextSerializer.serialize(itemStack.displayName())

    fun formatEnchantments(itemStack: ItemStack): String {
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

    fun formatCoordinate(value: Double) = "%.2f".format(Locale.ROOT, value)

    fun escapeTextLogValue(value: String) = value
        .replace("\\", "\\\\")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("|", "\\|")

    fun escapeAuxProtectDetailValue(value: Any?): String = value
        .toString()
        .replace("\\", "\\\\")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace(";", "\\;")
        .replace("=", "\\=")
}
