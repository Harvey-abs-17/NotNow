package com.hadi.abbasi.notnow.domain.model

@JvmInline
value class SourceNotificationKey(
    val value: String,
) {
    init {
        require(value.isNotBlank()) {
            "Source notification key must not be blank."
        }
    }
}