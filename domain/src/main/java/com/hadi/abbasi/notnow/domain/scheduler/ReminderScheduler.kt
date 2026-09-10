package com.hadi.abbasi.notnow.domain.scheduler

import com.hadi.abbasi.notnow.domain.model.Reminder
import com.hadi.abbasi.notnow.domain.model.ReminderId

interface ReminderScheduler {
    fun schedule(reminder: Reminder)
    fun cancel(reminderId: ReminderId)
}
