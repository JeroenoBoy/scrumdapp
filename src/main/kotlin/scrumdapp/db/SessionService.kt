package com.jeroenvdg.scrumdapp.db

import com.jeroenvdg.scrumdapp.Database.dbQuery
import com.jeroenvdg.scrumdapp.models.UserSession
import com.jeroenvdg.scrumdapp.models.UserTable.UserSessions
import io.ktor.util.date.GMTDate
import kotlinx.datetime.Instant
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertReturning
import kotlin.random.Random

interface SessionService {
    suspend fun getSession(token: String): UserSession?
    suspend fun createSession(userId: Int, refreshToken: String, accesToken: String, accesTokenExpiry: GMTDate): UserSession
    suspend fun revokeAllSessions(userId: Int): Result<Unit>
}

class SessionServiceImpl: SessionService {
    override suspend fun getSession(token: String): UserSession? {
        return dbQuery {
            val statement = UserSessions.select(UserSessions.fields).where(UserSessions.token eq token)
            resultRowToSession(statement.single())
        }
    }

    override suspend fun createSession(userId: Int, refreshToken: String, accesToken: String, accesTokenExpiry: GMTDate): UserSession {
        return dbQuery{
            val statement = UserSessions.insertReturning(UserSessions.fields) {
                it[this.userId] = userId
                it[token] = randomString(128)
                it[discordRefreshToken] = refreshToken
                it[discordAccessToken] = accesToken
                it[discordAccessTokenExpiry] = Instant.fromEpochMilliseconds(accesTokenExpiry.timestamp)
            }
            resultRowToSession(statement.single())
        }
    }

    override suspend fun revokeAllSessions(userId: Int): Result<Unit> {
        dbQuery {
            UserSessions.deleteWhere { this.userId eq userId }
        }
        return Result.success(Unit)
    }

    private fun resultRowToSession(resultRow: ResultRow): UserSession {
        return UserSession(
            resultRow[UserSessions.id],
            userId = resultRow[UserSessions.userId]!!,
            token = resultRow[UserSessions.token],
            discordRefreshToken = resultRow[UserSessions.discordRefreshToken],
            discordAccessToken = resultRow[UserSessions.discordAccessToken],
            discordAccessTokenExpiry = GMTDate(resultRow[UserSessions.discordAccessTokenExpiry].toEpochMilliseconds()),
            createdAt = GMTDate(resultRow[UserSessions.createdAt].toInstant(UtcOffset.ZERO).toEpochMilliseconds()),
        )
    }

    private fun randomString(length: Int): String {
        val nums = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_"
        return (1..length).joinToString("") { nums.random().toString() }
    }
}