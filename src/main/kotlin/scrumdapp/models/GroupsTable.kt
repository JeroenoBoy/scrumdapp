package com.jeroenvdg.scrumdapp.models

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.*

@Serializable
enum class Presence(val color: String, val key: String) {
    OnTime("green", "Op Tijd"),
    Late("yellow", "Te Laat"),
    VerifiedAbsent("green-dim", "Geoorloofd Afwezig"),
    Absent("red", "Ongeoorloofd Afwezig"),
    Sick("blue", "Ziek"),
}

class GroupsTable {
    object Groups: Table() {
        val id = integer("id").autoIncrement()
        val name = varchar("name", 50)
        val bannerPicture = varchar("bannerPicture", 50).nullable()
        override val primaryKey = PrimaryKey(id)
    }

    object UserGroups: Table() {
        val id = integer("id").autoIncrement()
        val groupId = optReference("group_id", Groups.id, onDelete = ReferenceOption.CASCADE)
        val userId = optReference("user_id", UserTable.Users.id, onDelete = ReferenceOption.CASCADE)
        val permissions = integer("permissions").default(69)

        override val primaryKey = PrimaryKey(id)
    }

    object GroupCheckins: Table() {
        val id = integer("id").autoIncrement()
        val groupId = optReference("group_id", Groups.id, onDelete = ReferenceOption.CASCADE)
        val userId = optReference("user_id", UserTable.Users.id, onDelete = ReferenceOption.CASCADE)
        val presence = enumeration("presence", Presence::class).nullable()
        val date = date("date")
        val checkinStars = integer("checkin_stars").check { it greaterEq 0 and(it lessEq 10) }.nullable()
        val checkupStars = integer("checkup_stars").check { it greaterEq 0 and(it lessEq 10) }.nullable()
        val comment = text("comment").nullable()

        override val primaryKey = PrimaryKey(id)
    }

    object GroupInvite: Table() {
        val id = integer("id").autoIncrement()
        val groupId = optReference("group_id", Groups.id, onDelete = ReferenceOption.CASCADE)
        val token = varchar("token", 64)
        val password = varchar("password", 255).nullable()
        val createdAt = datetime("created_at")

        override val primaryKey = PrimaryKey(id)
    }
}
