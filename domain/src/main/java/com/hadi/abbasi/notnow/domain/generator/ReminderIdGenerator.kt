package com.hadi.abbasi.notnow.domain.generator

import com.hadi.abbasi.notnow.domain.model.ReminderId

fun interface ReminderIdGenerator {
    fun generate(): ReminderId
}
