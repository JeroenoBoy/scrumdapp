package com.jeroenvdg.scrumdapp.models

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

class UserTable(database: Database) {
    object Users : Table() {
        val id = integer("id").autoIncrement()
        val name = varchar("name", length = 50)
        val discordId = long("discord_id")
        val profileImage = varchar("profile_image", length = 255) // Possible need to change this

        override val primaryKey = PrimaryKey(id)
    }

    object UserSessions: Table() {
        val id = integer("id").autoIncrement()
        val user_id = optReference("user_id", Users.id, onDelete = ReferenceOption.CASCADE)
        val token = varchar("token", 255)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Users, UserSessions)
        }
    }
}