package dev.slne.surf.skill.paper.commands

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.skill.core.progress.SkillProgressImpl
import dev.slne.surf.skill.api.manager.SkillManager
import dev.slne.surf.skill.api.progress.SkillProgress
import dev.slne.surf.skill.paper.menu.SkillsView
import dev.slne.surf.skill.paper.utils.SkillPermissionRegistry
import dev.slne.surf.surfapi.bukkit.api.inventory.framework.viewFrame
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.random
import it.unimi.dsi.fastutil.objects.ObjectList

fun skillCommand() = commandAPICommand("skill") {
    withPermission(SkillPermissionRegistry.COMMAND_SKILL)

    playerExecutor { player, arguments ->
        viewFrame.open(SkillsView::class.java, player, mapOf("skill_progress" to fakePlayerData()))
    }
}

private fun fakePlayerData(): ObjectList<SkillProgress> {
    val skills = SkillManager.skills
    val skillProgress = mutableObjectListOf<SkillProgress>()

    skills.forEach { skill ->
        skillProgress.add(
            SkillProgressImpl(
                skill = skill,
                currentExperience = random.nextInt(0, 10000)
            )
        )
    }

    return skillProgress
}