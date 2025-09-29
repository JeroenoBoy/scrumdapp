package com.scrumdapp.scrumdapp.middleware

import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.db.GroupService
import com.jeroenvdg.scrumdapp.db.UserGroup
import com.jeroenvdg.scrumdapp.middleware.hasUser
import com.jeroenvdg.scrumdapp.middleware.user
import com.jeroenvdg.scrumdapp.middleware.userSession
import com.jeroenvdg.scrumdapp.models.UserPermissions
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.response.respondRedirect
import io.ktor.util.AttributeKey

private val groupAttributeKey = AttributeKey<Group>("Current Group")
private val groupUserAttributeKey = AttributeKey<UserGroup>("Current Group User")

/**
 * Should be checked if it exists with the IsInGroup
 */
val ApplicationCall.group: Group
    get() = attributes[groupAttributeKey]

/**
 * Should be checked if it exists with the IsInGroup
 */
val ApplicationCall.groupUser: UserGroup
    get() = attributes[groupUserAttributeKey]

class GroupProviderConfig() {
    lateinit var groupService: GroupService
}

class PermProviderConfig() {
    var permissions: UserPermissions = UserPermissions.User
}

val IsInGroup = createRouteScopedPlugin("Group Provider", ::GroupProviderConfig) {
    val groupService = pluginConfig.groupService
    onCall { call ->
        if (!call.hasUser) return@onCall
        val userId = call.user.id
        val groupId = call.parameters["groupId"]?.toIntOrNull() ?: return@onCall call.respondRedirect("/home")
        val group = groupService.getGroup(groupId) ?: return@onCall call.respondRedirect("/home")
        val groupUser = groupService.getGroupUser(groupId, userId) ?: return@onCall call.respondRedirect("/home")
        call.attributes[groupAttributeKey] = group
        call.attributes[groupUserAttributeKey] = groupUser
    }
}

val HasCorrectPerms = createRouteScopedPlugin("HasCorrect Perms", ::PermProviderConfig) {
    val perm = pluginConfig.permissions
    onCall { call ->
        val groupUser = call.attributes.getOrNull(groupUserAttributeKey) ?: return@onCall call.respondRedirect("/home")
        if (perm.id <= groupUser.permissions.id) {
            call.respondRedirect("/groups/${call.group.id}")
        }
    }
}
