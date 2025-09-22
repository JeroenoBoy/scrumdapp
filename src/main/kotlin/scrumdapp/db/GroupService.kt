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
    suspend fun getGroupMemberPermissions(group: Group, userid: Int): UserPermissions
    suspend fun compareGroupMemberPermissions(group: Group, userid: Int, permission: UserPermissions): Boolean
    suspend fun createGroup(group: Group): Group?
    suspend fun addGroupMember(group: Group, user: User, permission: UserPermissions = UserPermissions.User)
    suspend fun alterGroupMemberPerms(user: User, permission: UserPermissions): Boolean
    suspend fun deleteGroupMember(group: Group, user: User): Boolean

    suspend fun createGroupInvite(group: Group, password: String): String
    suspend fun deleteGroupInvite(group: Group, user: User)
}