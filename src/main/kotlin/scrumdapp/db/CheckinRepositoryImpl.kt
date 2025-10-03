package com.jeroenvdg.scrumdapp.db

import com.jeroenvdg.scrumdapp.Database.dbQuery
import com.jeroenvdg.scrumdapp.models.GroupsTable.*
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.models.UserTable.Users
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class CheckinRepositoryImpl: CheckinRepository {
    private fun resultRowToCheckin(row: ResultRow): Checkin {
        return Checkin(
            id = row[GroupCheckins.id],
            groupId = row[GroupCheckins.groupId],
            name = row[Users.name],
            userId = row[GroupCheckins.userId],
            presence = row[GroupCheckins.presence],
            date = row[GroupCheckins.date],
            checkinStars = row[GroupCheckins.checkinStars],
            checkupStars = row[GroupCheckins.checkupStars],
            comment = row[GroupCheckins.comment]
        )
    }

    override suspend fun getUserCheckins(user: User, groupId: Int): List<Checkin> {
        return dbQuery {
            GroupCheckins
                .innerJoin(Users, { GroupCheckins.id eq user.id}, { Users.id})
                .select(GroupCheckins.fields + Users.name)
                .where {GroupCheckins.groupId eq groupId and(GroupCheckins.userId eq user.id)}
                .map { resultRowToCheckin(it) }
        }
    }

    override suspend fun getGroupCheckins(groupId: Int, date: LocalDate): List<Checkin> {
        return dbQuery {
            UserGroups
                .innerJoin(Users, { UserGroups.userId }, { Users.id }) // Get name
                .leftJoin(GroupCheckins, { UserGroups.userId }, { GroupCheckins.userId }, { (GroupCheckins.date eq date) and (GroupCheckins.groupId eq groupId) }) // Get checkin
                .select(GroupCheckins.fields + UserGroups.groupId + Users.id + Users.name)
                .where { (UserGroups.groupId eq groupId) and (UserGroups.permissions neq UserPermissions.Coach.id) }
                .map { Checkin(
                        id = it.getOrNull(GroupCheckins.id) ?: -1,
                        groupId = it[UserGroups.groupId],
                        name = it[Users.name],
                        userId = it[Users.id],
                        presence = it.getOrNull(GroupCheckins.presence),
                        date = it.getOrNull(GroupCheckins.date) ?: date,
                        checkinStars = it.getOrNull(GroupCheckins.checkinStars),
                        checkupStars = it.getOrNull(GroupCheckins.checkupStars),
                        comment = it.getOrNull(GroupCheckins.comment),
                    )
                }
                .sortedBy { it.name }
        }
    }

    override suspend fun getCheckinDates(groupId: Int, limit: Int): List<LocalDate> {
        return dbQuery {
            GroupCheckins
                .select(GroupCheckins.date)
                .where { GroupCheckins.groupId eq groupId }
                .orderBy(GroupCheckins.date to SortOrder.DESC)
                .withDistinctOn(GroupCheckins.date)
                .limit(limit)
                .map { it[GroupCheckins.date] }
        }
    }

    override suspend fun getCheckin(id: Int): Checkin? {
        return dbQuery {
            GroupCheckins
                .innerJoin(Users, { GroupCheckins.userId }, { Users.id })
                .select(GroupCheckins.fields + Users.name)
                .where {GroupCheckins.id eq id}
                .map { resultRowToCheckin(it) }
                .firstOrNull()
        }
    }

    override suspend fun createCheckin(checkin: Checkin): Checkin? {
        return dbQuery {
            val inserts = GroupCheckins.insert {
                it[GroupCheckins.groupId] = checkin.groupId
                it[GroupCheckins.userId] = checkin.userId
                it[GroupCheckins.presence] = checkin.presence
                it[GroupCheckins.date] = checkin.date
                it[GroupCheckins.checkinStars] = checkin.checkinStars
                it[GroupCheckins.checkupStars] = checkin.checkupStars
                it[GroupCheckins.comment] = checkin.comment
            }
            inserts.resultedValues?.singleOrNull()?.let { resultRowToCheckin(it) }
        }
    }

    override suspend fun createGroupCheckin(groupId: Int, checkins: List<Checkin>): List<Checkin>? {
        return dbQuery {
            GroupCheckins.batchInsert(checkins) { checkin ->
                this[GroupCheckins.groupId] = groupId
                this[GroupCheckins.userId] = checkin.userId
                this[GroupCheckins.presence] = checkin.presence
                this[GroupCheckins.date] = checkin.date
                this[GroupCheckins.checkinStars] = checkin.checkinStars
                this[GroupCheckins.checkupStars] = checkin.checkupStars
                this[GroupCheckins.comment] = checkin.comment
            }.map { resultRow -> resultRowToCheckin(resultRow) }.takeIf { it.isNotEmpty() }
        }
    }

    override suspend fun saveGroupCheckin(checkins: List<Checkin>) {
        return dbQuery {
            GroupCheckins.batchUpsert(checkins) { checkin ->
                if (checkin.id != -1) this[GroupCheckins.id] = checkin.id
                this[GroupCheckins.groupId] = checkin.groupId
                this[GroupCheckins.userId] = checkin.userId
                this[GroupCheckins.presence] = checkin.presence
                this[GroupCheckins.date] = checkin.date
                this[GroupCheckins.checkinStars] = checkin.checkinStars
                this[GroupCheckins.checkupStars] = checkin.checkupStars
                this[GroupCheckins.comment] = checkin.comment
            }
        }
    }

    override suspend fun alterCheckin(checkin: Checkin): Boolean {
        return dbQuery {
            GroupCheckins.update({ GroupCheckins.userId eq checkin.userId and (GroupCheckins.groupId eq checkin.groupId)}) {
               it[GroupCheckins.presence] = checkin.presence
               it[GroupCheckins.date] = checkin.date
               it[GroupCheckins.checkinStars] = checkin.checkinStars
               it[GroupCheckins.checkupStars] = checkin.checkupStars
                it[GroupCheckins.comment] = checkin.comment
            }>0
        }
    }

    override suspend fun deleteCheckin(checkin: Checkin): Boolean {
        return dbQuery {
            GroupCheckins.deleteWhere { GroupCheckins.groupId eq checkin.groupId and (GroupCheckins.userId eq checkin.userId)}>0
        }
    }
}