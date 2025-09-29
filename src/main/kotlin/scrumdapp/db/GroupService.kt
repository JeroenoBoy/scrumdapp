package com.jeroenvdg.scrumdapp.db

import com.jeroenvdg.scrumdapp.models.UserPermissions
import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val id: Int,
    val name: String,
)

interface GroupService {
    suspend fun allGroup(): List<Group>
    suspend fun getGroup(id: Int): Group?
    suspend fun getGroupMembers(id: Int): List<User>
    suspend fun getUserGroups(id: Int): List<Group>
    suspend fun getGroupMemberPermissions(groupId: Int, userid: Int): UserPermissions
    suspend fun compareGroupMemberAccess(groupId: Int, userid: Int): Boolean
    suspend fun compareGroupMemberPermissions(groupid: Int, userid: Int, permission: UserPermissions): Boolean
    suspend fun createGroup(group: Group): Group?
    suspend fun addGroupMember(groupId: Int, user: User, permission: UserPermissions = UserPermissions.User)
    suspend fun alterGroupMemberPerms(user: User, permission: UserPermissions): Boolean
    suspend fun deleteGroupMember(groupId: Int, user: User): Boolean

    suspend fun createGroupInvite(groupId: Int, password: String): String
    suspend fun deleteGroupInvite(groupId: Int, user: User)
}