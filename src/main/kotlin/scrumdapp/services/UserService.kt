package com.jeroenvdg.scrumdapp.services

import com.jeroenvdg.scrumdapp.db.CheckinRepository
import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.db.GroupRepository
import com.jeroenvdg.scrumdapp.db.User
import com.jeroenvdg.scrumdapp.db.UserGroup
import com.jeroenvdg.scrumdapp.models.UserPermissions


data class UserDashboardData(
    val groupMembers: List<User>,
    val groupUsers: List<UserGroup>,
)

class UserService(
    private val groupRepository: GroupRepository,
) {
    suspend fun getUserDashboardDate(groupId: Int): UserDashboardData{
        val groupMembers = groupRepository.getGroupMembers(groupId)
        val groupUsers = groupRepository.getGroupUsers(groupId)
        return UserDashboardData(groupMembers, groupUsers)
    }

    suspend fun alterUserPermissions(groupId: Int, permChanges: Map<Int, Int>, userPerm: UserPermissions): Boolean {
        for ((userId, permId) in permChanges) {
            if (userPerm.id < permId) {
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
        if (!groupUsers.any { it.id == targetUserId }) { return false }
        if (userPerm.id >= groupUsers.first { it.id == targetUserId }.permissions.id) { return false }

        try {
            groupRepository.deleteGroupMember(groupId, targetUserId)
            return true
        } catch (e: Exception) {
            throw ServerFaultException()
        }
    }

}