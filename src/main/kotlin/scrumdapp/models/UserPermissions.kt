package com.jeroenvdg.scrumdapp.models

import org.jetbrains.exposed.sql.ResultRow

enum class RoleColours {
    GREEN,
    YELLOW,
    BLUE,
    PURPLE,
    ORANGE,
    AQUA,
}

sealed class UserPermissions(val displayName: String, val id: Int, val colour: RoleColours, val description: String) {
    object LordOfScrum: UserPermissions(displayName = "Lord of Scrum", -2, RoleColours.GREEN, "Maker van de groep, kan alles wat de scrumdad kan + toewijzen alle rollen inclusief scrumdad.")
    object ScrumDad: UserPermissions("Scrumdad", -1, RoleColours.YELLOW, "Alles wat rolemangament kan + verwijderen groep en toewijzen van rollen.")
    object UserManagement: UserPermissions("Usermanagement", 0, RoleColours.BLUE,"Alles wat checkinmangement kan + verwijderen en uitnodigen van gebruikers.")
    object CheckinManagement: UserPermissions("Checkinmanagement", 1, RoleColours.PURPLE, "Alles wat gebruiker kan + checkins aanmaken en aanpassen.")
    object Coach: UserPermissions("Coach", 68, RoleColours.ORANGE,"Rol voor begeleider/coach, verschijnt niet in checkins en kan alle trends downloaden.")
    object User: UserPermissions("Gebruiker", 69, RoleColours.AQUA,"Standaard rol voor leden. Kan trends inzien en eigen trends downloaden.")

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