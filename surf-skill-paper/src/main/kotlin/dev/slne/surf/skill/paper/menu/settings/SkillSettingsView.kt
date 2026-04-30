package dev.slne.surf.skill.paper.menu.settings

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.paper.builder.buildItem
import dev.slne.surf.api.paper.builder.buildLore
import dev.slne.surf.api.paper.builder.displayName
import dev.slne.surf.api.paper.inventory.framework.dsl.onItemClick
import dev.slne.surf.api.paper.inventory.framework.dsl.openForPlayer
import dev.slne.surf.api.paper.inventory.framework.view.*
import dev.slne.surf.api.paper.inventory.framework.view.icon.ViewIconColor
import dev.slne.surf.api.paper.inventory.framework.view.icon.ViewIconType
import dev.slne.surf.api.paper.inventory.framework.view.icon.viewIcon
import dev.slne.surf.api.paper.inventory.framework.view.state.get
import dev.slne.surf.api.paper.inventory.framework.view.state.mutableState
import dev.slne.surf.api.paper.inventory.framework.view.state.set
import dev.slne.surf.skill.core.paper.settings.SettingsHook
import dev.slne.surf.skill.paper.menu.skillsView
import dev.slne.surf.skill.paper.plugin
import org.bukkit.Material

val skillSettingsView = surfView("Einstellungen") {
    val aState = mutableState(false)
    val bState = mutableState(false)
    val cState = mutableState(false)

    val aInitialState = mutableState(false)
    val bInitialState = mutableState(false)
    val cInitialState = mutableState(false)

    settings {
        rows(3)
        cancelAllInteractions()
    }

    onInit {
        layout(
            "         ",
            "  A B C  ",
            "    X    "
        )
    }

    onFirstRender {
        aInitialState[this] = SettingsHook.hasGainXpSoundEnabled(this.player.uniqueId)
        bInitialState[this] = SettingsHook.hasLevelUpSoundsEnabled(this.player.uniqueId)
        cInitialState[this] = SettingsHook.hasLevelUpMessagesEnabled(this.player.uniqueId)

        aState[this] = aInitialState[this]
        bState[this] = bInitialState[this]
        cState[this] = cInitialState[this]

        layoutSlot('A').renderWith { gainXpSoundItem(aState[this]) }.onClick { click ->
            aState[click] = !aState[click]
            update()

            this.player.sendText {
                appendSuccessPrefix()
                success("Du hast den Skill XP Sound " + if (aState[click]) "aktiviert." else "deaktiviert.")
            }
        }

        layoutSlot('B').renderWith { levelUpSoundItem(bState[this]) }.onClick { click ->
            bState[click] = !bState[click]
            update()

            this.player.sendText {
                appendSuccessPrefix()
                success("Du hast den Level Up Sound " + if (bState[click]) "aktiviert." else "deaktiviert.")
            }
        }

        layoutSlot('C').renderWith { levelUpMessageItem(cState[this]) }.onClick { click ->
            cState[click] = !cState[click]
            update()

            this.player.sendText {
                appendSuccessPrefix()
                success("Du hast die Level Up Nachrichten " + if (cState[click]) "aktiviert." else "deaktiviert.")
            }
        }

        layoutSlot('X', viewIcon(ViewIconType.HOME, ViewIconColor.RED) {
            displayName {
                primary("Zurück".toSmallCaps())
            }
        }).onItemClick {
            openForPlayer(skillsView)
        }
    }

    onClose {
        val playerUuid = this.player.uniqueId

        val aState = aState[this]
        val bState = bState[this]
        val cState = cState[this]

        val aInitialState = aInitialState[this]
        val bInitialState = bInitialState[this]
        val cInitialState = cInitialState[this]

        plugin.launch {
            if (aState != aInitialState) {
                SettingsHook.setGainXpSoundEnabled(playerUuid, aState)
            }

            if (bState != bInitialState) {
                SettingsHook.setLevelUpSoundsEnabled(playerUuid, bState)
            }

            if (cState != cInitialState) {
                SettingsHook.setLevelUpMessagesEnabled(playerUuid, cState)
            }
        }
    }
}

private fun gainXpSoundItem(enabled: Boolean) =
    buildItem(if (enabled) Material.LIME_CANDLE else Material.RED_CANDLE) {
        displayName {
            primary("Skill XP Sound")
        }

        buildLore {
            emptyLine()
            line {
                spacer("»")
                appendSpace()

                if (enabled) {
                    success("✔ Aktiviert")
                } else {
                    error("✘ Deaktiviert")
                }
            }
        }
    }

private fun levelUpSoundItem(enabled: Boolean) =
    buildItem(if (enabled) Material.LIME_CANDLE else Material.RED_CANDLE) {
        displayName {
            primary("Level Up Sound")
        }

        buildLore {
            emptyLine()
            line {
                spacer("»")
                appendSpace()

                if (enabled) {
                    success("✔ Aktiviert")
                } else {
                    error("✘ Deaktiviert")
                }
            }
        }
    }

private fun levelUpMessageItem(enabled: Boolean) =
    buildItem(if (enabled) Material.LIME_CANDLE else Material.RED_CANDLE) {
        displayName {
            primary("Level Up Nachrichten")
        }

        buildLore {
            emptyLine()
            line {
                spacer("»")
                appendSpace()

                if (enabled) {
                    success("✔ Aktiviert")
                } else {
                    error("✘ Deaktiviert")
                }
            }
        }
    }