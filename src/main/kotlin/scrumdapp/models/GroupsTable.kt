package com.jeroenvdg.scrumdapp.models

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and

class GroupsTable(database: Database) {
    object Groups: Table() {
        val id = integer("id").autoIncrement()
        val name = varchar("name", 50)

        override val primaryKey = PrimaryKey(id)
    }

    object UserGroups: Table() {
        val id = integer("id").autoIncrement()
        val groupId = optReference("group_id", Groups.id, onDelete = ReferenceOption.CASCADE)
        val userId = optReference("user_id", UserTable.Users.id, onDelete = ReferenceOption.CASCADE)
        val permissions = integer("permissions")

        override val primaryKey = PrimaryKey(id)
    }

    object GroupCheckin: Table() {
        val id = integer("id").autoIncrement()
        val groupId = optReference("group_id", Groups.id, onDelete = ReferenceOption.CASCADE)
        val userId = optReference("user_id", UserTable.Users.id, onDelete = ReferenceOption.CASCADE)
        val onTime = bool("on_time")
        val delay = integer("total_delay").default(0)
        val checkinStars = integer("checkin_stars").check { it greaterEq 0 and(it lessEq 5) }
        val checkupStars = integer("checkup_stars").check { it greaterEq 0 and(it lessEq 5) }
        val comment = text("comment")

        override val primaryKey = PrimaryKey(id)
    }

}
