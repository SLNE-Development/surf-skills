package dev.slne.surf.skill.backend.db.tables

import dev.slne.surf.database.columns.nativeUuid
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.ULongIdTable

object SkillExperiencesTable : ULongIdTable("skill_experiences") {
    val uuid = nativeUuid("uuid")

    val skillName = varchar("skill_name", 255)
    val experience = integer("experience")

    init {
        uniqueIndex(uuid, skillName)
    }
}