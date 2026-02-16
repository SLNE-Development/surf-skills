package dev.slne.surf.skill.api

import dev.slne.surf.skill.api.curve.ExperienceCurve
import dev.slne.surf.skill.api.experience.SkillExperience
import dev.slne.surf.skill.api.level.SkillLevel
import dev.slne.surf.surfapi.bukkit.api.builder.LoreBuilder
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ItemType
import java.util.*

interface Skill : ComponentLike {
    val name: String
    val displayName: Component
    val lore: LoreBuilder.() -> Unit
    val material: ItemType

    val experienceCurve: ExperienceCurve
    val baseExperience: Int
    val maxExperience: Int
    val maxLevel: Int

    fun getLevels(): ObjectList<SkillLevel>
    fun displayItemStack(progress: SkillExperience): ItemStack

    suspend fun awardLevelUpRewards(uuid: UUID, level: Int)

    companion object {
        const val BASE_EXPERIENCE = 100
        const val MAX_SKILL_LEVEL = 50
        const val MAX_EXPERIENCE = 5_000_000
    }
}