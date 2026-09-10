package com.hadi.abbasi.notnow.domain.usecase

import com.hadi.abbasi.notnow.domain.generator.ReminderIdGenerator
import com.hadi.abbasi.notnow.domain.model.NotificationSnapshot
import com.hadi.abbasi.notnow.domain.model.Reminder
import com.hadi.abbasi.notnow.domain.model.ReminderStatus
import com.hadi.abbasi.notnow.domain.repository.NotificationRepository
import com.hadi.abbasi.notnow.domain.repository.ReminderRepository
import com.hadi.abbasi.notnow.domain.scheduler.ReminderScheduler
import java.time.Clock
import java.time.Instant
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class CreateReminderUseCase(
    private val notificationRepository: NotificationRepository,
    private val reminderRepository: ReminderRepository,
    private val reminderScheduler: ReminderScheduler,
    private val reminderIdGenerator: ReminderIdGenerator,
    private val clock: Clock,
) {
    suspend operator fun invoke(
        request: CreateReminderRequest,
    ): CreateReminderResult {
        val now = clock.instant()
        if (!request.scheduledAt.isAfter(now)) {
            return CreateReminderResult.InvalidScheduledTime
        }

        return try {
            createReminder(request = request, createdAt = now)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (cause: Throwable) {
            CreateReminderResult.Failure(cause)
        }
    }

    private suspend fun createReminder(
        request: CreateReminderRequest,
        createdAt: Instant,
    ): CreateReminderResult {
        val snapshot = notificationRepository
            .observeById(request.notificationSnapshotId)
            .first()
            ?: return CreateReminderResult.NotificationNotFound

        val reminder = snapshot.toReminder(
            request = request,
            createdAt = createdAt,
        )

        persistAndSchedule(reminder, snapshot)

        return CreateReminderResult.Success(reminder)
    }

    private suspend fun persistAndSchedule(
        reminder: Reminder,
        snapshot: NotificationSnapshot,
    ) {
        reminderRepository.save(reminder = reminder)

        try {
            reminderScheduler.schedule(reminder = reminder)
            notificationRepository.delete(notificationSnapshotId = snapshot.id)
        } catch (cause: Throwable) {
            rollbackReminder(reminder = reminder, originalFailure = cause)
            throw cause
        }
    }

    private fun NotificationSnapshot.toReminder(
        request: CreateReminderRequest,
        createdAt: Instant,
    ) = Reminder(
        id = reminderIdGenerator.generate(),
        notificationSnapshotId = id,
        sourcePackageName = sourcePackageName,
        sourceAppName = sourceAppName,
        title = title,
        body = body,
        scheduledAt = request.scheduledAt,
        createdAt = createdAt,
        status = ReminderStatus.SCHEDULED,
    )

    private suspend fun rollbackReminder(
        reminder: Reminder,
        originalFailure: Throwable,
    ) = withContext(NonCancellable) {
        runCatching {
            reminderScheduler.cancel(reminder.id)
        }.exceptionOrNull()?.let(originalFailure::addSuppressed)

        try {
            reminderRepository.delete(reminder.id)
        } catch (rollbackFailure: Throwable) {
            originalFailure.addSuppressed(rollbackFailure)
        }
    }
}
