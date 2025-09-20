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
data class DiscordUserResponse(var expires: String, var scopes: List<String>, var user: DiscordUser)

@Serializable
@JsonIgnoreUnknownKeys
data class DiscordUser(var id: String, var username: String, var avatar: String, var global_name: String)

@Serializable
@JsonIgnoreUnknownKeys
data class DiscordGuild(var id: String, var name: String, var icon: String?)

interface DiscordService {
    suspend fun getUser(token: String): DiscordUser
    suspend fun getGuilds(token: String): List<DiscordGuild>
}

class DiscordServiceImpl(val client: HttpClient) : DiscordService {
    override suspend fun getUser(token: String): DiscordUser {
        val response = client.get("https://discord.com/api/v10/users/@me") {
            header("authorization", "Bearer $token")
        }
        val body = Json.decodeFromString<DiscordUser>(response.bodyAsText())
        return body
    }

    override suspend fun getGuilds(token: String): List<DiscordGuild> {
        val response = client.get("https://discord.com/api/v10/users/@me/guilds") {
            header("authorization", "Bearer $token")
        }
        println(response.bodyAsText())
        val body = Json.decodeFromString<List<DiscordGuild>>(response.bodyAsText())
        return body
    }
}