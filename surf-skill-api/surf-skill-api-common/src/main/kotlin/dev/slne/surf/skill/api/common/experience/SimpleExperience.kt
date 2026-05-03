package dev.slne.surf.skill.api.common.experience

import dev.slne.surf.api.core.serializer.java.uuid.SerializableUUID
import kotlinx.serialization.Serializable

@Serializable
data class SimpleExperience(
    val uuid: SerializableUUID,
    val skillName: String,
    val currentExperience: Int
)
