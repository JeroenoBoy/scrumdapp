package com.jeroenvdg.scrumdapp.db

import com.jeroenvdg.scrumdapp.Database.dbQuery
import com.jeroenvdg.scrumdapp.UserService.Users.name
import com.jeroenvdg.scrumdapp.models.UserTable.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
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
            Users.select(Users.id eq id).map { resultRowToUser(it) }.singleOrNull()
        }
    }

    override suspend fun getUsers(): List<User> {
        return dbQuery {
            Users.selectAll().map { resultRowToUser(it) }
        }
    }

    override suspend fun addUser(user: Users): User? {
        return dbQuery {
            val inserts = Users.insert {
                it[name] = user.name
                it[discordId] = user.discordId
                it[profileImage] = user.profileImage
            }
            inserts.resultedValues?.singleOrNull()?.let { resultRowToUser(it) }
        }
    }

    override suspend fun alterUser(user: Users): Boolean {
        return dbQuery {
            Users.update ({Users.id eq user.id}) {
                it[name] = user.name
                it[discordId] = user.discordId
                it[profileImage] = user.profileImage
            }>0
        }
    }

    override suspend fun deleteUser(user: Users): Boolean {
        return dbQuery {
            Users.deleteWhere { Users.id eq user.id }
        }>0
    }
}