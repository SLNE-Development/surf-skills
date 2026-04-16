package dev.slne.surf.skill.microservice.repository

import dev.slne.surf.api.core.util.toMutableObjectList
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.upsert
import dev.slne.surf.skill.api.common.experience.SimpleExperience
import dev.slne.surf.skill.microservice.table.SkillExperiencesTable
import it.unimi.dsi.fastutil.objects.ObjectList
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import java.util.*

object ExperienceRepository {
    suspend fun findExperiences(uuid: UUID): ObjectList<SimpleExperience> = suspendTransaction {
        SkillExperiencesTable.selectAll()
            .where { SkillExperiencesTable.uuid eq uuid }
            .mapNotNull {
                val skillName = it[SkillExperiencesTable.skillName]
                val experience = it[SkillExperiencesTable.experience]

                SimpleExperience(
                    uuid = uuid,
                    skillName = skillName,
                    currentExperience = experience,
                )
            }.toList().toMutableObjectList()
    }

    suspend fun saveExperience(
        uuid: UUID,
        skillExperience: SimpleExperience
    ) = suspendTransaction {
        val skillName = skillExperience.skillName
        val experience = skillExperience.currentExperience

        SkillExperiencesTable.upsert {
            it[SkillExperiencesTable.uuid] = uuid
            it[SkillExperiencesTable.skillName] = skillName
            it[SkillExperiencesTable.experience] = experience
        }
    }
}