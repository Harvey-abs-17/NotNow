package com.hadi.abbasi.notnow.domain.repository

import com.hadi.abbasi.notnow.domain.model.NotificationSnapshot
import com.hadi.abbasi.notnow.domain.model.NotificationSnapshotId
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun save(notificationSnapshot: NotificationSnapshot)
    fun observeAll(): Flow<List<NotificationSnapshot>>
    fun observeById(notificationSnapshotId: NotificationSnapshotId): Flow<NotificationSnapshot?>
    suspend fun delete(notificationSnapshotId: NotificationSnapshotId)
}