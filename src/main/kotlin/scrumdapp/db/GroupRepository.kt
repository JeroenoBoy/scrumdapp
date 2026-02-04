package com.jeroenvdg.scrumdapp.db

import com.jeroenvdg.scrumdapp.models.UserPermissions
import kotlinx.datetime.LocalDateTime

data class Group(
    val id: Int,
    val name: String,
    val bannerImage: String?,
)

data class GroupUser(
    val id: Int,
    val groupId: Int,
    val user: User,
    val permissions: UserPermissions,
)

data class Groupinvite(
    val id: Int,
    val groupId: Int,
    val token: String,
    val createdAt: LocalDateTime,
    val password: String?,
)

interface GroupRepository {
    suspend fun allGroup(): List<Group>
    suspend fun getGroup(id: Int): Group?
    suspend fun getGroupNotes(id: Int): String?
    suspend fun saveGroupNotes(id: Int, notes: String?)
    suspend fun getGroupMembers(id: Int): List<User>
    suspend fun getGroupsUsersContainingUser(userId: Int): List<GroupUser>
    suspend fun getUserGroups(id: Int): List<Group>
    suspend fun getGroupUser(groupId: Int, userId: Int): GroupUser?
    suspend fun getGroupUsers(groupId: Int): List<GroupUser>
    suspend fun getGroupMemberPermissions(groupId: Int, userid: Int): UserPermissions
    suspend fun compareGroupMemberAccess(groupId: Int, userid: Int): Boolean
    suspend fun compareGroupMemberPermissions(groupId: Int, userid: Int, permission: UserPermissions): Boolean
    suspend fun createGroup(group: Group): Group?
    suspend fun updateGroup(groupId: Int, name: String? = null, bannerImage: String? = null)
    suspend fun deleteGroup(groupId: Int)
    suspend fun addGroupMember(groupId: Int, userId: Int, permission: UserPermissions = UserPermissions.User)
    suspend fun alterGroupMemberPerms(groupId: Int, userId: Int, permission: UserPermissions): Boolean
    suspend fun deleteGroupMember(groupId: Int, userId: Int): Boolean
    suspend fun getGroupInvite(token: String): Groupinvite?
    suspend fun createGroupInvite(groupId: Int, token: String, password: String?)
    suspend fun deleteGroupInvite(inviteId: Int): Boolean
}