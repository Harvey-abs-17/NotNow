package com.hadi.abbasi.notnow.domain.model

@JvmInline
value class NotificationSnapshotId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) {
            "Notification snapshot ID must not be blank."
        }
    }
}