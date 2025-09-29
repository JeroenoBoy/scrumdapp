package com.jeroenvdg.scrumdapp.models

import org.jetbrains.exposed.sql.ResultRow

sealed class UserPermissions(val displayName: String, val id: Int) {
    object LordOfScrum: UserPermissions(displayName = "Lord_of_the_Scrum", -2) // The maker of the group
    object ScrumDad: UserPermissions("scrumdad", -1) // cool way of saying admin
    object UserManagement: UserPermissions("user_management", 0) // allows for editing or adding users
    object CheckinManagement: UserPermissions("checkin_management", 1) // allows for editing or adding checkins
    object User: UserPermissions("user", 69) // standard developer role

    companion object {
        fun fromId(row: ResultRow): UserPermissions {
            return when (row[GroupsTable.UserGroups.permissions]) {
                -2 -> LordOfScrum
                -1 -> ScrumDad
                0 -> UserManagement
                1 -> CheckinManagement
                69 -> User
                else -> User
            }
        }
    }
}