package com.hadi.abbasi.notnow.domain.usecase

import com.hadi.abbasi.notnow.domain.model.Reminder

sealed interface CreateReminderResult {
    data class Success(val reminder: Reminder) : CreateReminderResult

    data object NotificationNotFound : CreateReminderResult

    data object InvalidScheduledTime : CreateReminderResult

    data class Failure(val cause: Throwable) : CreateReminderResult
}
