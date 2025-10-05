package com.jeroenvdg.scrumdapp.models

import io.ktor.util.date.GMTDate
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction


@Serializable
data class UserSession(
    var id: Int,
    var userId: Int,
    var token: String,
    var discordRefreshToken: String,
    var discordAccessToken: String?,
    var discordAccessTokenExpiry: GMTDate,
    val createdAt: GMTDate
);

class UserTable {
    object Users : Table() {
        val id = integer("id").autoIncrement()
        val name = varchar("name", length = 50)
        val discordId = long("discord_id").uniqueIndex()
        val profileImage = varchar("profile_image", length = 255) // Possible need to change this

        override val primaryKey = PrimaryKey(id)
    }

    object UserSessions: Table() {
        val id = integer("id").autoIncrement()
        val userId = optReference("user_id", Users.id, onDelete = ReferenceOption.CASCADE)
        val token = varchar("token", 128)
        val discordRefreshToken = varchar("discord_refresh_token", 64)
        val discordAccessToken = varchar("discord_access_token", 64).nullable()
        val discordAccessTokenExpiry = timestamp("discord_access_token_expire_date")
        val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

        override val primaryKey = PrimaryKey(id)
    }
}