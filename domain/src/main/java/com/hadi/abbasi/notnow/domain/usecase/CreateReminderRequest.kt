package com.hadi.abbasi.notnow.domain.usecase

import com.hadi.abbasi.notnow.domain.model.NotificationSnapshotId
import java.time.Instant

data class CreateReminderRequest(
    val notificationSnapshotId: NotificationSnapshotId,
    val scheduledAt: Instant,
)
