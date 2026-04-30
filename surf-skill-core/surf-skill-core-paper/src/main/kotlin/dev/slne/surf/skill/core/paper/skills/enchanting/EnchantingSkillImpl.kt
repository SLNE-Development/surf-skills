@file:Suppress("UnstableApiUsage")

package dev.slne.surf.skill.core.paper.skills.enchanting

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.util.objectListOf
import dev.slne.surf.skill.api.paper.skills.EnchantingSkill
import dev.slne.surf.skill.core.paper.AbstractSkill
import dev.slne.surf.skill.core.paper.skills.enchanting.listeners.EnchantListener
import dev.slne.surf.skill.core.paper.skills.enchanting.listeners.EnchantingAbilityListener
import org.bukkit.inventory.ItemType

@AutoService(EnchantingSkill::class)
class EnchantingSkillImpl : AbstractSkill(
    name = "enchanting",
    niceName = "???",
    material = ItemType.ENCHANTED_BOOK,
    displayName = buildText {
        primary("???".toSmallCaps())
    },
    lore = {},
    listeners = objectListOf(EnchantListener, EnchantingAbilityListener),
    abilities = objectListOf(),
    active = false
), EnchantingSkill