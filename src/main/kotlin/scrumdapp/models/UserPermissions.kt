package com.jeroenvdg.scrumdapp.models

sealed class UserPermissions(val displayName: String, val id: Int) {
    object ScrumDad: UserPermissions("scrumdad", -1) // cool way of saying admin
    object UserManagement: UserPermissions("user_management", 0) // allows for editing or adding users
    object CheckinManagement: UserPermissions("checkin_management", 1) // allows for editing or adding checkins
    object User: UserPermissions("user", 69) // standard developer role

    companion object {
        fun fromId(id: Int): UserPermissions = when (id) {
            -1 -> ScrumDad
            0 -> UserManagement
            1 -> CheckinManagement
            69 -> User
            else -> User
        }
    }
}