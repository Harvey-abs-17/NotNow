package com.hadi.abbasi.notnow.domain.model

@JvmInline
value class ReminderId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) {
            "Reminder ID must not be blank."
        }
    }
}