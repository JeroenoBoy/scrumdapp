@file:OptIn(ExperimentalSerializationApi::class)

package com.jeroenvdg.scrumdapp.services.oauth2.discord

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Serializable
@JsonIgnoreUnknownKeys
data class DiscordUserResponse(val expires: String, val scopes: List<String>, val user: DiscordUser)

@Serializable
@JsonIgnoreUnknownKeys
data class DiscordUser(val id: String, val username: String, val avatar: String?, val global_name: String)

@Serializable
@JsonIgnoreUnknownKeys
data class DiscordGuild(val id: String, val name: String, val icon: String?)

@Serializable
@JsonIgnoreUnknownKeys
data class DiscordGuildMember(val user: DiscordUser?, val nick: String?, val avatar: String?)

interface DiscordService {
    suspend fun getUser(token: String): Result<DiscordUser>
    suspend fun getGuilds(token: String): Result<List<DiscordGuild>>
    suspend fun getGuildMember(token: String, guildId: String): Result<DiscordGuildMember>
}

class DiscordServiceException(val status: Int, message: String = "") : Exception(message)

class DiscordServiceImpl(val client: HttpClient) : DiscordService {
    override suspend fun getUser(token: String): Result<DiscordUser> {
        return discordGetRequest("/users/@me", token)
    }

    override suspend fun getGuilds(token: String): Result<List<DiscordGuild>> {
        return discordGetRequest("/users/@me/guilds", token)
    }

    override suspend fun getGuildMember(token: String, guildId: String): Result<DiscordGuildMember> {
        return discordGetRequest("/users/@me/guilds/$guildId/member", token)
    }

    private suspend inline fun <reified T> discordGetRequest(endpoint: String, token: String): Result<T> {
        val response = client.get("https://discord.com/api/v10$endpoint") {
            header("authorization", "Bearer $token")
        }
        if (response.status == HttpStatusCode.OK) {
            val body = Json.decodeFromString<T>(response.bodyAsText())
            return Result.success(body)
        }
        println(response.bodyAsText())
        return Result.failure(DiscordServiceException(response.status.value, response.status.description))
    }
}
