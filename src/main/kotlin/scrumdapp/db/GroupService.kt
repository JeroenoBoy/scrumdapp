package com.jeroenvdg.scrumdapp.db

import com.jeroenvdg.scrumdapp.models.UserPermissions

data class Group(
    val id: Int,
    val name: String,
)

interface GroupService {
    suspend fun allGroup(): List<Group>
    suspend fun getGroup(id: Int): Group?
    suspend fun getGroupMembers(id: Int): List<User>
    suspend fun createGroup(group: Group): Group?
    suspend fun addGroupMember(groupId: Int, user: User)
    suspend fun alterGroupMemberPerms(userId: Int, permission: UserPermissions)
    suspend fun deleteGroupMember(groupId: Int, user: User)

    suspend fun createGroupInvite(groupId: Int, password: String): String
    suspend fun deleteGroupInvite(groupId: Int, user: User)
}