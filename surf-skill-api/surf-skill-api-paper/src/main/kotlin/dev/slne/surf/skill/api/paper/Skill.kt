package dev.slne.surf.skill.api.paper

import dev.slne.surf.api.paper.builder.LoreBuilder
import dev.slne.surf.skill.api.common.InternalSkillApi
import dev.slne.surf.skill.api.common.curve.ExperienceCurve
import dev.slne.surf.skill.api.paper.experience.SkillExperience
import dev.slne.surf.skill.api.paper.level.SkillLevel
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ItemType
import org.jetbrains.annotations.Unmodifiable
import java.util.*

interface Skill : ComponentLike {
    val name: String
    val niceName: String
    val displayName: Component
    val lore: LoreBuilder.() -> Unit
    val material: ItemType

    val experienceCurve: ExperienceCurve
    val baseExperience: Int
    val maxExperience: Int
    val maxLevel: Int
    val active: Boolean

    fun getLevels(): ObjectList<SkillLevel>
    fun displayItemStack(progress: SkillExperience): ItemStack

    suspend fun awardLevelUpRewards(uuid: UUID, level: Int)

    @InternalSkillApi
    val listeners: @Unmodifiable ObjectList<Listener>

    @InternalSkillApi
    fun registerListeners()

    companion object {
        const val BASE_EXPERIENCE = 100
        const val MAX_SKILL_LEVEL = 50
        const val MAX_EXPERIENCE = 5_000_000
    }
}