package com.jeroenvdg.scrumdapp.models

import org.jetbrains.exposed.sql.ResultRow

sealed class UserPermissions(val displayName: String, val id: Int) {
    object LordOfScrum: UserPermissions(displayName = "Lord of Scrum", -2) // The maker of the group
    object ScrumDad: UserPermissions("Scrumdad", -1) // cool way of saying admin
    object UserManagement: UserPermissions("Usermanagement", 0) // allows for editing or adding users
    object CheckinManagement: UserPermissions("Checkinmanagement", 1) // allows for editing or adding checkins
    object Coach: UserPermissions("Coach", 68) // allows for editing or adding checkins
    object User: UserPermissions("Gebruiker", 69) // standard developer role

    companion object {
        fun fromId(row: ResultRow): UserPermissions {
            return when (row[GroupsTable.UserGroups.permissions]) {
                -2 -> LordOfScrum
                -1 -> ScrumDad
                0 -> UserManagement
                1 -> CheckinManagement
                68 -> Coach
                69 -> User
                else -> User
            }
        }

        fun get(id: Int): UserPermissions {
            return when (id) {
                -2 -> LordOfScrum
                -1 -> ScrumDad
                0 -> UserManagement
                1 -> CheckinManagement
                68 -> Coach
                69 -> User
                else -> User
            }
        }

        fun canExportPresence(userPermissions: UserPermissions, isSelf: Boolean): Boolean {
            return userPermissions == Coach || isSelf
        }
    }
}