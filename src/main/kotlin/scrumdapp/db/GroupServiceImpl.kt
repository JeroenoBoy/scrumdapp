package com.jeroenvdg.scrumdapp.db

import com.jeroenvdg.scrumdapp.Database.dbQuery
import com.jeroenvdg.scrumdapp.models.GroupsTable.*
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.models.UserTable.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class GroupServiceImpl: GroupService {
    private fun resultRowToGroup(row: ResultRow): Group {
        return Group(
            id = row[Groups.id],
            name = row[Groups.name],
        )
    }

    private fun resultRowToUser(row: ResultRow): User {
        return User(
            id = row[Users.id],
            name = row[Users.name],
            discordId = row[Users.discordId],
            profileImage = row[Users.profileImage],
        )
    }

    private fun resultRowToUserGroup(row: ResultRow): UserGroup {
        return UserGroup(
            groupId = row[UserGroups.groupId]?: -1,
            userId = row[UserGroups.userId]?: -1,
            permission = row[UserGroups.permissions]
        )
    }

    override suspend fun allGroup(): List<Group> {
        return dbQuery { Groups.selectAll().map { resultRowToGroup(it) } }
    }

    override suspend fun getGroup(id: Int): Group? {
        return dbQuery {
            Groups.select(Groups.fields).where { Groups.id eq id }
            .map { resultRowToGroup(it)}.singleOrNull()}
    }

    override suspend fun createGroup(group: Group): Group? {
        return dbQuery {
            val inserts = Groups.insert {
                it[name]=group.name
            }
            inserts.resultedValues?.singleOrNull()?.let { resultRowToGroup(it) }
        }
    }

    override suspend fun getGroupMembers(id: Int): List<User> {
        return dbQuery {
            (UserGroups innerJoin Users)
                .select(Users.fields).where { UserGroups.groupId eq id }
                .map { resultRowToUser(it) }
        }
    }

    override suspend fun getUserGroups(id: Int): List<Group> {
        return dbQuery {
            (UserGroups innerJoin Groups)
                .select(Groups.fields)
                .where { UserGroups.userId eq id }
                .map { resultRowToGroup(it)}
        }
    }

    override suspend fun getUserGroupMembers(groupId: Int): List<UserGroup> {
        return dbQuery {
            UserGroups.select(UserGroups.columns)
                .where { UserGroups.id eq groupId }
                .map { resultRowToUserGroup(it)}
        }
    }

    override suspend fun getGroupMemberPermissions(groupId: Int, userid: Int): UserPermissions {
        return dbQuery {
            UserGroups
                .select(UserGroups.permissions)
                .where { UserGroups.userId eq userid and (UserGroups.groupId eq groupId) }
                .withDistinct()
                .map { UserPermissions.fromId(it) }
                .first()
        }
    }

    override suspend fun compareGroupMemberAccess(groupId: Int, userid: Int): Boolean {
        return dbQuery {
            UserGroups.select(UserGroups.userId)
                .where { UserGroups.groupId eq groupId and (UserGroups.userId eq userid) }
                .any()
        }
    }

    override suspend fun compareGroupMemberPermissions(groupId: Int, userid: Int, permission: UserPermissions): Boolean {
        val result = dbQuery {
            UserGroups.select(UserGroups.permissions)
                .where { UserGroups.groupId eq groupId and (UserGroups.userId eq userid) }
        }
        return result == permission
    }

    override suspend fun addGroupMember(groupid: Int, user: User, permission: UserPermissions) {
        return dbQuery {
            UserGroups.insert {
                it[groupId] = groupid
                it[userId] = user.id
                it[permissions] = permission.id
            }
        }
    }

    override suspend fun alterGroupMemberPerms(user: User, permission: UserPermissions): Boolean {
        return dbQuery {
            UserGroups.update({ UserGroups.userId eq user.id}) {
                it[permissions]=permission.id
            }>0
        }
    }

    override suspend fun deleteGroupMember(groupId: Int, user: User): Boolean {
        return dbQuery {
            UserGroups.deleteWhere { UserGroups.groupId eq groupId and (UserGroups.userId eq user.id) }>0
        }
    }

    override suspend fun createGroupInvite(
        groupId: Int,
        password: String
    ): String {
        TODO("Not yet implemented")
    }

    override suspend fun deleteGroupInvite(groupId: Int, user: User) { // Not finished, discuss with Jeroen
        return dbQuery {
            GroupInvite.deleteWhere { GroupInvite.groupId eq groupId }
        }
    }


}