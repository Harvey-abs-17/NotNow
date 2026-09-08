package com.hadi.abbasi.notnow.domain.model

import java.time.Instant

data class NotificationSnapshot(
    val id: NotificationSnapshotId,
    val sourceNotificationKey: SourceNotificationKey,
    val sourcePackageName: String,
    val sourceAppName: String,
    val title: String?,
    val body: String?,
    val postedAt: Instant,
    val capturedAt: Instant,
)