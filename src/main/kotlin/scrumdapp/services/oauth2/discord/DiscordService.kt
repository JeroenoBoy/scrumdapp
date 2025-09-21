@file:OptIn(ExperimentalSerializationApi::class)

package com.jeroenvdg.scrumdapp.services.oauth2.discord

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.async
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

class DiscordServiceImpl(val client: HttpClient) : DiscordService {
    override suspend fun getUser(token: String): Result<DiscordUser> {
        val response = client.get("https://discord.com/api/v10/users/@me") {
            header("authorization", "Bearer $token")
        }
        val body = Json.decodeFromString<DiscordUser>(response.bodyAsText())
        return Result.success(body)
    }

    override suspend fun getGuilds(token: String): Result<List<DiscordGuild>> {
        val response = client.get("https://discord.com/api/v10/users/@me/guilds") {
            header("authorization", "Bearer $token")
        }
        val body = Json.decodeFromString<List<DiscordGuild>>(response.bodyAsText())
        return Result.success(body)
    }

    override suspend fun getGuildMember(token: String, guildId: String): Result<DiscordGuildMember> {
        val response = client.get("https://discord.com/api/v10/users/@me/guilds/$guildId/member") {
            header("authorization", "Bearer $token")
        }
        val body = Json.decodeFromString<DiscordGuildMember>(response.bodyAsText())
        return Result.success(body)
    }
}