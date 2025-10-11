package com.jeroenvdg.scrumdapp.middleware

import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.db.GroupRepository
import com.jeroenvdg.scrumdapp.db.GroupUser
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.utils.resolveBlocking
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respondRedirect
import io.ktor.util.AttributeKey

private val groupAttributeKey = AttributeKey<Group>("Current Group")
private val groupUserAttributeKey = AttributeKey<GroupUser>("Current Group User")

/**
 * Should be checked if it exists with the IsInGroup
 */
val ApplicationCall.group: Group
    get() = attributes[groupAttributeKey]

/**
 * Should be checked if it exists with the IsInGroup
 */
val ApplicationCall.groupUser: GroupUser
    get() = attributes[groupUserAttributeKey]

class PermProviderConfig() {
    var permissions: UserPermissions = UserPermissions.User
}

val IsInGroup = createRouteScopedPlugin("Group Provider") {
    val groupRepository = application.dependencies.resolveBlocking<GroupRepository>()
    onCall { call ->
        if (!call.hasUser) return@onCall
        val userId = call.user.id
        val groupId = call.parameters["groupId"]?.toIntOrNull() ?: return@onCall call.respondRedirect("/home")
        val group = groupRepository.getGroup(groupId) ?: return@onCall call.respondRedirect("/home")
        val groupUser = groupRepository.getGroupUser(groupId, userId) ?: return@onCall call.respondRedirect("/home")
        call.attributes[groupAttributeKey] = group
        call.attributes[groupUserAttributeKey] = groupUser
    }
}

val HasCorrectPerms = createRouteScopedPlugin("HasCorrect Perms", ::PermProviderConfig) {
    val perm = pluginConfig.permissions
    onCall { call ->
        val groupUser = call.attributes.getOrNull(groupUserAttributeKey) ?: return@onCall call.respondRedirect("/home")
        if (perm.id < groupUser.permissions.id) {
            call.respondRedirect("/groups/${call.group.id}")
        }
    }
}
