package com.hadi.abbasi.notnow.domain.repository

import com.hadi.abbasi.notnow.domain.model.Reminder
import com.hadi.abbasi.notnow.domain.model.ReminderId
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    suspend fun save(reminder: Reminder)
    fun observeActive(): Flow<List<Reminder>>
    fun observeById(reminderId: ReminderId): Flow<Reminder?>
    suspend fun delete(reminderId: ReminderId)
}