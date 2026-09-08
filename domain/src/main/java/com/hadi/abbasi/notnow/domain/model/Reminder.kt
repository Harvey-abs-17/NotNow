package com.hadi.abbasi.notnow.domain.model

import java.time.Instant

data class Reminder(
    val id: ReminderId,
    val notificationSnapshotId: NotificationSnapshotId,
    val sourcePackageName: String,
    val sourceAppName: String,
    val title: String?,
    val body: String?,
    val scheduledAt: Instant,
    val createdAt: Instant,
    val status: ReminderStatus,
)
