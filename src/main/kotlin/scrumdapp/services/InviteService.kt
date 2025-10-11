package com.jeroenvdg.scrumdapp.services

import com.jeroenvdg.scrumdapp.db.GroupRepository
import com.jeroenvdg.scrumdapp.db.Groupinvite

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class InviteService(
    private val groupRepository: GroupRepository,
    private val encryptionService: EncryptionService
) {
    private fun CheckTokenExpiry(targetDate: LocalDateTime, days: Int): Boolean {
        val expiryDate = Clock.System.now().plus(days * 24, DateTimeUnit.HOUR).toLocalDateTime(TimeZone.currentSystemDefault())
        return targetDate >= expiryDate

    }

    suspend fun checkGroupTokenAccess(userId: Int, invite: Groupinvite, password: String): Boolean {
        if (CheckTokenExpiry(invite.createdAt, 14)) {
            groupRepository.deleteGroupInvite(invite.id)
            return false
        }

        if (encryptionService.compareHash(password, invite.password?: "")) {
            val groupUsers = groupRepository.getGroupUsers(invite.groupId)
            if (groupUsers.any { it.user.id == userId }) return true

            groupRepository.addGroupMember(invite.groupId, userId)
            return true
        } else {
            return false
        }
    }

    suspend fun createInviteToken(password: String?, groupId: Int): String? {
        val passwordRegex = Regex("^[a-zA-Z0-9_ .,#^!?><]{3,50}")
        if (password.isNullOrBlank() || !passwordRegex.containsMatchIn(password)) return null

        val token = encryptionService.generateRandomToken(60)
        groupRepository.createGroupInvite(groupId, token, encryptionService.hashValue(password))
        return token
    }
}