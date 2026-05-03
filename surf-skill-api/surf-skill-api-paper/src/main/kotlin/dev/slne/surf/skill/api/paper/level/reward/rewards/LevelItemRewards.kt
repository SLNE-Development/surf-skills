package dev.slne.surf.skill.api.paper.level.reward.rewards

import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.Colors
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.playSound
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.util.mutableObjectListOf
import dev.slne.surf.api.paper.builder.LoreBuilder
import dev.slne.surf.skill.api.paper.level.reward.AbstractLevelReward
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.inventory.meta.SuspiciousStewMeta
import org.bukkit.potion.PotionEffect
import java.util.Locale
import org.bukkit.Sound as BukkitSound

class LevelItemRewards(
    val itemStacks: ObjectList<ItemStack>
) : AbstractLevelReward(
    displayName = buildText {
        primary("Items:".toSmallCaps())
    },
    description = {
        buildItemDescription(itemStacks)
    }
) {
    override suspend fun grant(player: Player) {
        val notAddedItemStacks = mutableObjectListOf<ItemStack>()

        itemStacks.forEach { itemStack ->
            val notAdded = player.inventory.addItem(itemStack)

            if (notAdded.isEmpty()) {
                RewardGrantLogger.log(player, itemStack, RewardDelivery.INVENTORY)
            } else {
                notAddedItemStacks.addAll(notAdded.values)

                val addedAmount = itemStack.amount - notAdded.values.sumOf { it.amount }
                if (addedAmount > 0) {
                    RewardGrantLogger.log(
                        player,
                        itemStack.asQuantity(addedAmount),
                        RewardDelivery.INVENTORY
                    )
                }
            }
        }

        notAddedItemStacks.forEach { notAdded ->
            player.world.dropItem(player.location, notAdded) { item ->
                item.isInvulnerable = true
                item.pickupDelay = 0
                item.owner = player.uniqueId
            }
            RewardGrantLogger.log(player, notAdded, RewardDelivery.DROPPED)
        }

        if (notAddedItemStacks.isNotEmpty()) {
            player.sendText {
                appendErrorPrefix()

                error("Dein Inventar ist voll!", TextDecoration.BOLD)
                appendSpace()
                error("Die Belohnungen, die nicht in dein Inventar gelegt werden konnten, wurden auf den Boden vor dir fallen gelassen.")
            }

            player.playSound(true) {
                type(BukkitSound.ENTITY_VILLAGER_HURT)
                volume(0.5f)
                source(Sound.Source.MASTER)
            }
        }
    }

    companion object {
        private fun LoreBuilder.buildItemDescription(itemStacks: ObjectList<ItemStack>) {
            itemStacks.forEach { buildItemStackDescription(it) }
        }

        private fun LoreBuilder.buildItemStackDescription(itemStack: ItemStack) {
            val itemStackDisplayName = itemStack.displayName()
            val itemMeta = itemStack.itemMeta
            val lore = itemMeta.lore() ?: emptyList()

            line {
                spacer("- ")
                append(itemStackDisplayName)
            }

            if (lore.isNotEmpty()) {
                lore.forEach {
                    line {
                        spacer("  - ")
                        append(it)
                    }
                }
            }

            buildEnchantmentsDescription(itemStack.enchantments)

            if (itemMeta is EnchantmentStorageMeta) {
                buildEnchantmentsDescription(itemMeta.storedEnchants)
            }

            when (itemMeta) {
                is PotionMeta -> buildPotionEffectsDescription(itemMeta.customEffects)
                is SuspiciousStewMeta -> buildPotionEffectsDescription(itemMeta.customEffects)
            }
        }

        private fun LoreBuilder.buildEnchantmentsDescription(enchantments: Map<Enchantment, Int>) {
            if (enchantments.isEmpty()) return

            line {
                spacer("  - ")
                spacer("Verzauberungen:".toSmallCaps())
            }

            enchantments.forEach { (enchantment, level) ->
                line {
                    spacer("    - ")
                    append(enchantment.displayName(level)).color(Colors.SPACER)
                }
            }
        }

        private fun LoreBuilder.buildPotionEffectsDescription(effects: List<PotionEffect>) {
            if (effects.isEmpty()) return

            line {
                spacer("  - ")
                spacer("Effekte:".toSmallCaps())
            }

            effects.forEach { effect ->
                line {
                    spacer("    - ")
                    append(Component.text(formatPotionEffect(effect))).color(Colors.SPACER)
                }
            }
        }

        private fun formatPotionEffect(effect: PotionEffect): String {
            val effectName = effect.type.name
                .lowercase(Locale.GERMAN)
                .split('_')
                .joinToString(" ") { part -> part.replaceFirstChar { it.titlecase(Locale.GERMAN) } }
            val amplifier = effect.amplifier + 1
            val amplifierText = if (amplifier > 1) " ${toRomanNumeral(amplifier)}" else ""

            return "$effectName$amplifierText (${formatDuration(effect.duration)})"
        }

        private fun formatDuration(ticks: Int): String {
            val totalSeconds = ((ticks.coerceAtLeast(0) + 19) / 20).coerceAtLeast(1)
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60

            return if (hours > 0) {
                "%02d:%02d:%02d".format(hours, minutes, seconds)
            } else {
                "%02d:%02d".format(minutes, seconds)
            }
        }

        private fun toRomanNumeral(value: Int): String {
            val numerals = listOf(
                1000 to "M",
                900 to "CM",
                500 to "D",
                400 to "CD",
                100 to "C",
                90 to "XC",
                50 to "L",
                40 to "XL",
                10 to "X",
                9 to "IX",
                5 to "V",
                4 to "IV",
                1 to "I"
            )

            var remaining = value
            val result = StringBuilder()

            numerals.forEach { (number, numeral) ->
                while (remaining >= number) {
                    result.append(numeral)
                    remaining -= number
                }
            }

            return result.toString()
        }
    }
}