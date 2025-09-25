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
                it[id]=group.id
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

    override suspend fun getGroupMemberPermissions(group: Group, userid: Int): UserPermissions {
        return dbQuery {
            UserGroups
                .select(UserGroups.fields).where { UserGroups.userId eq userid and (UserGroups.groupId eq group.id) }
                .withDistinct()
                .map { UserPermissions.fromId(it) }
                .single()
        }
    }

    override suspend fun compareGroupMemberPermissions(group: Group, userid: Int, permission: UserPermissions): Boolean {
        val result = dbQuery {
            UserGroups.select(UserGroups.permissions)
                .where { UserGroups.groupId eq group.id and (UserGroups.userId eq userid) }
        }
        return result == permission
    }

    override suspend fun addGroupMember(group: Group, user: User, permission: UserPermissions) {
        return dbQuery {
            UserGroups.insert {
                it[groupId] = group.id
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

    override suspend fun deleteGroupMember(group: Group, user: User): Boolean {
        return dbQuery {
            UserGroups.deleteWhere { UserGroups.groupId eq group.id and (UserGroups.userId eq user.id) }>0
        }
    }

    override suspend fun createGroupInvite(
        group: Group,
        password: String
    ): String {
        TODO("Not yet implemented")
    }

    override suspend fun deleteGroupInvite(group: Group, user: User) { // Not finished, discuss with Jeroen
        return dbQuery {
            GroupInvite.deleteWhere { GroupInvite.groupId eq group.id }
        }
    }


}