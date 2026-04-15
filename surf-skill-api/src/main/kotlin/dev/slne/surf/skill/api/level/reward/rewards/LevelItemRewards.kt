package dev.slne.surf.skill.api.level.reward.rewards

import dev.slne.surf.skill.api.level.reward.AbstractLevelReward
import dev.slne.surf.api.paper.builder.LoreBuilder
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.Colors
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.playSound
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.util.mutableObjectListOf
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
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

        itemStacks.forEach {
            val notAdded = player.inventory.addItem(it)

            if (notAdded.isNotEmpty()) {
                notAddedItemStacks.addAll(notAdded.values)
            }
        }

        notAddedItemStacks.forEach { notAdded ->
            player.world.dropItem(player.location, notAdded) { item ->
                item.isInvulnerable = true
                item.pickupDelay = 0
                item.owner = player.uniqueId
            }
        }

        if (notAddedItemStacks.isNotEmpty()) {
            player.sendText {
                appendErrorPrefix()

                error("Dein Inventar ist voll!", TextDecoration.BOLD)
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

            if (itemMeta is EnchantmentStorageMeta) {
                val enchantments = itemMeta.storedEnchants

                if (enchantments.isNotEmpty()) {
                    line {
                        spacer("  - ")
                        spacer("Verzauberungen:".toSmallCaps())
                    }

                    line {
                        spacer("    - ")
                        append(enchantments.map { it.key.displayName(it.value) }).color(Colors.SPACER)
                    }
                }
            }
        }
    }
}