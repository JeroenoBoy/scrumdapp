package com.jeroenvdg.scrumdapp.db

import com.jeroenvdg.scrumdapp.models.GroupsTable
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.models.UserTable

interface GroupService {
    suspend fun allGroup(): List<GroupsTable.Groups>
    suspend fun getGroup(id: Int): GroupsTable.Groups?
    suspend fun getGroupMembers(id: Int): List<UserTable.Users>
    suspend fun addGroupMember(groupId: Int, user: UserTable)
    suspend fun alterGroupMemberPerms(userId: Int, permission: UserPermissions)
    suspend fun deleteGroupMember(groupId: Int, user: UserTable)

    suspend fun createGroupInvite(groupId: Int, password: String): String
    suspend fun deleteGroupInvite(groupId: Int, user: UserTable)
}