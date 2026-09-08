package com.hadi.abbasi.notnow.domain.model

import java.time.Instant

data class Reminder(
    val id: ReminderId,
    val notificationSnapshotId: NotificationSnapshotId,
    val scheduledAt: Instant,
    val createdAt: Instant,
    val status: ReminderStatus,
)