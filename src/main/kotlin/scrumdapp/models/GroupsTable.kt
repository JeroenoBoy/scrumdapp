package com.jeroenvdg.scrumdapp.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.kotlin.datetime.date

@Serializable
enum class Presence {
    OnTime,
    Late,
    Absent,
    VerifiedAbsent,
    Sick,
}

class GroupsTable(database: Database) {
    object Groups: Table() {
        val id = integer("id").autoIncrement()
        val name = varchar("name", 50)
        val bannerPicture = varchar("bannerPicture", 50)
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
        val presence = enumeration("presence", Presence::class)
        val date = date("date")
        val delay = integer("total_delay").default(0)
        val checkinStars = integer("checkin_stars").check { it greaterEq 0 and(it lessEq 10) }
        val checkupStars = integer("checkup_stars").check { it greaterEq 0 and(it lessEq 10) }
        val comment = text("comment")

        override val primaryKey = PrimaryKey(id)
    }

    object GroupInvite: Table() {
        val id = integer("id").autoIncrement()
        val groupId = optReference("group_id", Groups.id, onDelete = ReferenceOption.CASCADE)
        val token = varchar("token", 64)
        val password = varchar("password", 255).nullable()

        override val primaryKey = PrimaryKey(id)
    }
}
