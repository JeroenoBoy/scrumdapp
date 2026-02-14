package com.jeroenvdg.scrumdapp.models

import org.jetbrains.exposed.sql.ResultRow

sealed class UserPermissions(val displayName: String, val id: Int, val description: String) {
    object LordOfScrum: UserPermissions(displayName = "Lord of Scrum", -2, "Maker van de groep, heeft alle rechten en de kan groep verwijderen.")
    object ScrumDad: UserPermissions("Scrumdad", -1, "Kan alles doen wat de Lord of Scrum kan min het verwijderen van de groep.")
    object UserManagement: UserPermissions("Usermanagement", 0, "Kan uitnodigingen maken, leden verwijderen en permissies geven om checkins aan te maken.")
    object CheckinManagement: UserPermissions("Checkinmanagement", 1, "Kan checkins aanmaken en bewerken.")
    object Coach: UserPermissions("Coach", 68, "Rol voor begeleider/coach, verschijnt niet in checkins en kan alle trends downloaden.")
    object User: UserPermissions("Gebruiker", 69, "Standaard rol voor leden. Kan eigen trends downloaden.")

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

        fun getAll(): List<UserPermissions> {
            return listOf(
                LordOfScrum,
                ScrumDad,
                UserManagement,
                CheckinManagement,
                Coach,
                User
            )
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