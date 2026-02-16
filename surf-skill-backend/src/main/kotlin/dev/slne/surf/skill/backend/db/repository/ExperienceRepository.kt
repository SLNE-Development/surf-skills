package dev.slne.surf.skill.backend.db.repository

import dev.slne.skill.core.experience.SkillExperienceImpl
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.and
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.upsert
import dev.slne.surf.skill.api.manager.SkillManager
import dev.slne.surf.skill.api.progress.SkillExperience
import dev.slne.surf.skill.backend.db.tables.SkillExperiencesTable
import dev.slne.surf.surfapi.core.api.util.toObjectList
import it.unimi.dsi.fastutil.objects.ObjectList
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import java.util.*

object ExperienceRepository {
    suspend fun findExperiences(uuid: UUID): ObjectList<SkillExperienceImpl> = suspendTransaction {
        SkillExperiencesTable.selectAll()
            .where { SkillExperiencesTable.uuid eq uuid }
            .mapNotNull {
                val skillName = it[SkillExperiencesTable.skillName]
                val experience = it[SkillExperiencesTable.experience]

                val skill = SkillManager.getSkillByName(skillName) ?: return@mapNotNull null

                SkillExperienceImpl(
                    skill = skill,
                    currentExperience = experience,
                )
            }.toList().toObjectList()
    }

    suspend fun saveExperience(
        uuid: UUID,
        skillExperience: SkillExperience
    ) = suspendTransaction {
        val skillName = skillExperience.skill.name
        val experience = skillExperience.currentExperience

        SkillExperiencesTable.upsert(
            where = { (SkillExperiencesTable.uuid eq uuid) and (SkillExperiencesTable.skillName eq skillName) },
        ) {
            it[SkillExperiencesTable.uuid] = uuid
            it[SkillExperiencesTable.skillName] = skillName
            it[SkillExperiencesTable.experience] = experience
        }
    }
}