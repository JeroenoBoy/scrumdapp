package com.jeroenvdg.scrumdapp.services

import com.jeroenvdg.scrumdapp.db.GroupRepository
import com.jeroenvdg.scrumdapp.db.GroupUser
import com.jeroenvdg.scrumdapp.middleware.ComparePermissions
import com.jeroenvdg.scrumdapp.models.UserPermissions

class GroupService(
    private val groupRepository: GroupRepository,
) {
    suspend fun alterUserPermissions(groupId: Int, permChanges: Map<Int, Int>, user: GroupUser): Boolean {
        for ((userId, permId) in permChanges) {
            if (user.permissions.id < permId) {
                try {
                    groupRepository.alterGroupMemberPerms(groupId, userId, UserPermissions.get(permId))
                } catch (e: Exception) {
                    throw ServerFaultException(message = "Er is iets misgegaan bij aan het aanpassen van de permissies.")
                }
            } else {
                return false
            }
        }
        return true
    }

    suspend fun deleteUserFromGroup(groupId: Int, targetUserId: Int, userPerm: UserPermissions): Boolean {
        val groupUsers = groupRepository.getGroupUsers(groupId)
        if (!groupUsers.any { it.user.id == targetUserId }) { return false }
        if (!ComparePermissions(userPerm, groupUsers.first { it.user.id == targetUserId}.permissions)) { return false }
        try {
            groupRepository.deleteGroupMember(groupId, targetUserId)
            return true
        } catch (e: Exception) {
            throw ServerFaultException()
        }
    }

}