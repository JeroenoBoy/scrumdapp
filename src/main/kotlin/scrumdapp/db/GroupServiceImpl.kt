package com.jeroenvdg.scrumdapp.db

import com.jeroenvdg.scrumdapp.Database.dbQuery
import com.jeroenvdg.scrumdapp.models.GroupsTable.*
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.models.UserTable.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class GroupServiceImpl: GroupService {

    private fun resultRowToGroup(row: ResultRow): Group {
        return Group( // Why break?
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
            Groups.select(Groups.id eq id)
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
                .select(UserGroups.groupId eq id)
                .map { resultRowToUser(it) }
        }
    }

    override suspend fun addGroupMember(groupId: Int, user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun alterGroupMemberPerms(
        userId: Int,
        permission: UserPermissions
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteGroupMember(groupId: Int, user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun createGroupInvite(groupId: Int, password: String): String {
        TODO("Not yet implemented")
    }

    override suspend fun deleteGroupInvite(groupId: Int, user: User) {
        TODO("Not yet implemented")
    }

}