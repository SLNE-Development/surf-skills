package dev.slne.surf.skill.microservice.table

import dev.slne.surf.database.columns.nativeUuid
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.Table

object SkillExperiencesTable : Table("skill_experiences") {
    val uuid = nativeUuid("uuid")

    val skillName = varchar("skill_name", 255)
    val experience = integer("experience")

    override val primaryKey = PrimaryKey(uuid, skillName)
}