package com.jeroenvdg.scrumdapp.utils

import com.jeroenvdg.scrumdapp.db.Checkin
import com.jeroenvdg.scrumdapp.models.UserPermissions

fun List<Checkin>.isNewCheckin(): Boolean {
    return this.all { it.id == -1 }
}