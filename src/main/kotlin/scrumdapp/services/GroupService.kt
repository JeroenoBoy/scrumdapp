package com.jeroenvdg.scrumdapp.services

import com.jeroenvdg.scrumdapp.db.GroupRepository
import com.jeroenvdg.scrumdapp.db.GroupUser
import com.jeroenvdg.scrumdapp.models.UserPermissions

class GroupService(
    private val groupRepository: GroupRepository,
) {
    suspend fun alterUserPermissions(groupId: Int, permChanges: Map<Int, Int>, user: GroupUser): Boolean {
        for ((userId, permId) in permChanges) {
            if (user.permissions.id < permId || userId != user.id) {
                val success = groupRepository.alterGroupMemberPerms(groupId, userId, UserPermissions.get(permId))
                if (!success) { return false }
            } else {
                return false
            }
        }
        return true
    }

    suspend fun deleteUserFromGroup(groupId: Int, targetUserId: Int, userPerm: UserPermissions): Boolean {
        val groupUsers = groupRepository.getGroupUsers(groupId)
        if (!groupUsers.any { it.id == targetUserId }) { return false }
        if (userPerm.id >= groupUsers.first { it.id == targetUserId }.permissions.id) { return false }

        try {
            groupRepository.deleteGroupMember(groupId, targetUserId)
            return true
        } catch (e: Exception) {
            // Figure out a way to throw exceptions
            return false
        }
    }

}