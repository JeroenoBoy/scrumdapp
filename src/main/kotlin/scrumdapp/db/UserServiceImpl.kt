package com.jeroenvdg.scrumdapp.db

import com.jeroenvdg.scrumdapp.Database.dbQuery
import com.jeroenvdg.scrumdapp.models.UserTable.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertReturning
import org.jetbrains.exposed.sql.update

class UserServiceImpl: UserService {
    private fun resultRowToUser(row: ResultRow): User {
        return User(
            id = row[Users.id],
            name = row[Users.name],
            discordId = row[Users.discordId],
            profileImage = row[Users.profileImage],
        )
    }

    override suspend fun getUser(id: Int): User? {
        return dbQuery {
            Users.select(Users.fields)
                .where(Users.id eq id)
                .singleOrNull()?.let { resultRowToUser(it) } }
    }

    override suspend fun getUserFromDiscordId(discordId: String): User? {
        val ldId = discordId.toLongOrNull()
        if (ldId == null) { return null }
        return dbQuery {
            Users.select(Users.fields)
                .where(Users.discordId eq ldId)
                .singleOrNull()?.let { resultRowToUser(it) } }
    }

    override suspend fun getUsers(): List<User> {
        return dbQuery {
            Users.selectAll().map { resultRowToUser(it) }
        }
    }

    override suspend fun addUser(user: User): User? {
        return dbQuery {
            val inserts = Users.insertReturning(Users.fields) {
                it[name] = user.name
                it[discordId] = user.discordId
                it[profileImage] = user.profileImage
            }
            inserts.singleOrNull()?.let { resultRowToUser(it) }
        }
    }

    override suspend fun alterUser(user: User): Boolean {
        return dbQuery {
            Users.update ({Users.id eq user.id}) {
                it[name] = user.name
                it[discordId] = user.discordId
                it[profileImage] = user.profileImage
            }>0
        }
    }

    override suspend fun deleteUser(user: User): Boolean {
        return dbQuery {
            Users.deleteWhere { Users.id eq user.id }
        }>0
    }
}