package com.jeroenvdg.scrumdapp.db

import com.jeroenvdg.scrumdapp.Database.dbQuery
import com.jeroenvdg.scrumdapp.models.GroupsTable.*
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.models.UserTable.*
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.format.DateTimeFormatter

class GroupRepositoryImpl: GroupRepository {
    private fun resultRowToGroup(row: ResultRow): Group {
        return Group(
            id = row[Groups.id],
            name = row[Groups.name],
            bannerImage = row[Groups.bannerPicture],
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

    private fun resultRowToInvite(row: ResultRow): Groupinvite {
        return Groupinvite(
            id = row[GroupInvite.id],
            groupId = row[GroupInvite.groupId]?: -1,
            token = row[GroupInvite.token],
            createdAt = row[GroupInvite.createdAt],
            password = row[GroupInvite.password],
        )
    }

    private fun groupUser(row: ResultRow): UserGroup {
        return UserGroup(
            id = row[UserGroups.id],
            userId = row[UserGroups.userId]!!,
            groupId = row[UserGroups.groupId]!!,
            permissions = UserPermissions.get(row[UserGroups.permissions])
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
                it[name] = group.name
                it[bannerPicture] = group.bannerImage
            }
            inserts.resultedValues?.singleOrNull()?.let { resultRowToGroup(it) }
        }
    }

    override suspend fun updateGroup(groupId: Int, name: String?, bannerImage: String?) {
        if (name == null && bannerImage == null) { return }
        dbQuery {
            Groups.update({ Groups.id eq groupId }) {
                if (name != null) { it[Groups.name] = name }
                if (bannerImage != null) { it[Groups.bannerPicture] = bannerImage }
            }
        }
    }

    override suspend fun deleteGroup(groupId: Int) {
        dbQuery {
            Groups.deleteWhere { Groups.id eq groupId }
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

    override suspend fun getGroupUser(groupId: Int, userId: Int): UserGroup? {
        return dbQuery {
            val groupUser = UserGroups
                .select(UserGroups.fields)
                .where { (UserGroups.groupId eq groupId) and (UserGroups.userId eq userId) }
                .singleOrNull()
            if (groupUser == null) null else groupUser(groupUser)
        }
    }

    override suspend fun getGroupUsers(groupId: Int): List<UserGroup> {
        return dbQuery {
            UserGroups.select(UserGroups.fields)
                .where(UserGroups.groupId eq groupId)
                .map { groupUser(it)}
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

    override suspend fun addGroupMember(groupId: Int, userId: Int, permission: UserPermissions) {
        return dbQuery {
            UserGroups.insert {
                it[this.groupId] = groupId
                it[this.userId] = userId
                it[permissions] = permission.id
            }
        }
    }

    override suspend fun alterGroupMemberPerms(groupId: Int, userId: Int, permission: UserPermissions): Boolean {
        return dbQuery {
            UserGroups.update({ UserGroups.userId eq userId and(UserGroups.groupId eq groupId) }) {
                it[permissions]=permission.id
            }>0
        }
    }

    override suspend fun deleteGroupMember(groupId: Int, userId: Int): Boolean {
        return dbQuery {
            UserGroups.deleteWhere { UserGroups.groupId eq groupId and (UserGroups.userId eq userId) }>0
        }
    }

    override suspend fun getGroupInvite(token: String): Groupinvite? {
        return dbQuery {
            GroupInvite.select(GroupInvite.fields)
                .where { GroupInvite.token eq token }
                .map { resultRowToInvite(it)}
                .first()
        }
    }

    override suspend fun createGroupInvite(groupId: Int, token: String, password: String?) {
        val createdDate = LocalDate.parse(java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
         return dbQuery {
            GroupInvite.insert {
                it[this.groupId] = groupId
                it[this.token] = token
                it[this.password] = password
                it[createdAt] = createdDate
            }
        }
    }


    override suspend fun deleteGroupInvite(groupId: Int, user: User) { // Not finished, discuss with Jeroen
        return dbQuery {
            GroupInvite.deleteWhere { GroupInvite.groupId eq groupId }
        }
    }


}