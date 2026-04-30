package dev.slne.surf.skill.core.paper.settings

import dev.slne.surf.api.paper.util.namespacedKey
import dev.slne.surf.settings.api.SurfSettingsApi
import dev.slne.surf.settings.api.setting.SettingKey
import java.util.*

object SettingsHook {
    private val levelUpMessagesKey = SettingKey.ofBoolean(namespacedKey("level-up-messages"), true)
    private val levelUpSoundsKey = SettingKey.ofBoolean(namespacedKey("levle-up-sounds"), true)
    private val gainXpSound = SettingKey.ofBoolean(namespacedKey("gain-xp-sound"), true)

    fun hasLevelUpMessagesEnabled(playerUuid: UUID) =
        SurfSettingsApi.getSettingValue(playerUuid, levelUpMessagesKey)

    fun hasLevelUpSoundsEnabled(playerUuid: UUID) =
        SurfSettingsApi.getSettingValue(playerUuid, levelUpSoundsKey)

    fun hasGainXpSoundEnabled(playerUuid: UUID) =
        SurfSettingsApi.getSettingValue(playerUuid, gainXpSound)


    suspend fun setLevelUpMessagesEnabled(playerUuid: UUID, enabled: Boolean) =
        SurfSettingsApi.saveSetting(playerUuid, levelUpMessagesKey, enabled)

    suspend fun setLevelUpSoundsEnabled(playerUuid: UUID, enabled: Boolean) =
        SurfSettingsApi.saveSetting(playerUuid, levelUpSoundsKey, enabled)

    suspend fun setGainXpSoundEnabled(playerUuid: UUID, enabled: Boolean) =
        SurfSettingsApi.saveSetting(playerUuid, gainXpSound, enabled)


    suspend fun registerSettings() {
        SurfSettingsApi.createSetting(levelUpMessagesKey)
        SurfSettingsApi.createSetting(levelUpSoundsKey)
        SurfSettingsApi.createSetting(gainXpSound)
    }
}