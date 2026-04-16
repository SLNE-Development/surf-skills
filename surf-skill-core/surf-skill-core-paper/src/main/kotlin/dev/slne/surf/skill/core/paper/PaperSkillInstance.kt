package dev.slne.surf.skill.core.paper

import dev.slne.surf.skill.api.paper.SkillInstance

interface PaperSkillInstance : SkillInstance {
    val paperLoader: PaperLoader
    val rabbitApi get() = paperLoader.rabbitApi
    
    companion object : PaperSkillInstance by SkillInstance.INSTANCE as PaperSkillInstance {
        val INSTANCE get() = SkillInstance.INSTANCE as PaperSkillInstance
    }
}