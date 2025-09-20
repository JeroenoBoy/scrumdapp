package com.jeroenvdg.scrumdapp.db

import com.jeroenvdg.scrumdapp.Database.dbQuery
import com.jeroenvdg.scrumdapp.models.GroupsTable.*
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.models.UserTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class GroupServiceImpl: GroupService {

    private fun resultRowToGroup(row: ResultRow): Groups {
        return Groups( // Why break?
            id = row[Groups.id],
            name = row[Groups.name],
        )
    }

    override suspend fun allGroup(): List<Groups> {
        return dbQuery { Groups.selectAll().map { resultRowToGroup(it) } }
    }

    override suspend fun getGroup(id: Int): Groups? {
        return dbQuery { Groups.select{(Groups.id eq id)}.map { resultRowToGroup(it)}.singleOrNull()}
    }

    override suspend fun getGroupMembers(id: Int): List<UserTable.Users> {
        TODO("Not yet implemented")
    }

    override suspend fun addGroupMember(groupId: Int, user: UserTable) {
        TODO("Not yet implemented")
    }

    override suspend fun alterGroupMemberPerms(
        userId: Int,
        permission: UserPermissions
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteGroupMember(groupId: Int, user: UserTable) {
        TODO("Not yet implemented")
    }

    override suspend fun createGroupInvite(groupId: Int, password: String): String {
        TODO("Not yet implemented")
    }

    override suspend fun deleteGroupInvite(groupId: Int, user: UserTable) {
        TODO("Not yet implemented")
    }

}