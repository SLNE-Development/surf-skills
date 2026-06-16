package dev.slne.surf.skill.core.paper.util

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.api.paper.pdc.block.pdc
import dev.slne.surf.skill.api.paper.Skill
import dev.slne.surf.skill.api.paper.SkillInstance
import dev.slne.surf.skill.api.paper.player.incrementExperience
import dev.slne.surf.skill.api.paper.player.skillPlayer
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.BlockType
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.ItemType
import org.bukkit.persistence.PersistentDataType
import kotlin.time.Duration.Companion.seconds

object BlockExperienceHandler {
    private val MODIFIED_BLOCK_KEY = NamespacedKey("surf", "skill_modified_block")

    val blockBreakCache = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterWrite(10.seconds)
        .build<BlockBreakEvent, Boolean>()

    fun handleBlockPlace(event: BlockPlaceEvent) {
        event.block.setUneligibleForExperience()
    }

    fun handleBlockBreak(event: BlockBreakEvent) {
        val block = event.block

        blockBreakCache.put(event, block.isEligibleForExperience())
        block.setEligibleForExperience()
    }

    inline fun <reified S : Skill> handleBlockDropItem(
        event: BlockDropItemEvent,
        experienceFunction: (ItemType, BlockType) -> Int
    ) {
        val (_, eligible) = findCacheByBlock(event.block) ?: return
        if (!eligible) return

        val totalExperience = event.items.mapNotNull {
            val blockType = event.blockState.type.asBlockType() ?: return@mapNotNull null
            val itemType = it.itemStack.type.asItemType() ?: return@mapNotNull null

            experienceFunction(itemType, blockType)
        }.sum()

        if (totalExperience <= 0) return

        SkillInstance.launch {
            event.player.skillPlayer().incrementExperience<S>(totalExperience)
        }
    }

    fun findCacheByBlock(block: Block) = blockBreakCache.asMap()
        .entries
        .firstOrNull { it.key.block.location == block.location }

    fun isEligibleForExperience(block: Block) = !block.pdc().has(MODIFIED_BLOCK_KEY)

    /**
     * Resolves block eligibility for [BlockDropItemEvent]-based perks from the cached eligibility
     * captured during [handleBlockBreak]. The block's current PDC state is unreliable here because
     * [handleBlockBreak] marks the block eligible before that event fires.
     */
    fun wasEligibleForExperience(block: Block): Boolean {
        return findCacheByBlock(block)?.value == true
    }

    fun setEligibleForExperience(block: Block) {
        block.pdc().remove(MODIFIED_BLOCK_KEY)
    }

    fun setUneligibleForExperience(block: Block) {
        block.pdc().set(MODIFIED_BLOCK_KEY, PersistentDataType.BOOLEAN, true)
    }
}

fun Block.isEligibleForExperience() = BlockExperienceHandler.isEligibleForExperience(this)
fun Block.wasEligibleForExperience() = BlockExperienceHandler.wasEligibleForExperience(this)
fun Block.setEligibleForExperience() = BlockExperienceHandler.setEligibleForExperience(this)
fun Block.setUneligibleForExperience() = BlockExperienceHandler.setUneligibleForExperience(this)