# NotNow — UX Specification v0.1

Status: Approved baseline for v0.1

This document defines the user-visible behavior of the first release. The project brief remains the source for the broader product vision.

## 1. Primary navigation

The app has three top-level destinations:

```text
Inbox | Reminders | Settings
```

`Create Reminder` and `Reminder Details` are secondary destinations opened from the top-level screens.

## 2. Inbox

### Entry point

In v0.1, users create reminders only from the NotNow Inbox. A direct action inside another app's Android notification is out of scope.

### Included notifications

- Only notifications received after Notification Access is enabled are captured.
- Newest items appear first.
- Each item shows the source app, title, body preview, and received time.
- Selecting an item opens Create Reminder.

### Deterministic exclusions

- NotNow's own notifications
- Ongoing notifications
- Group summaries where an actionable child is available
- Notifications with neither a useful title nor useful body

### Lifecycle

- Creating a reminder removes the source item from Inbox and adds the reminder to Upcoming.
- Each captured notification can have at most one active reminder.
- Users can remove an item from Inbox without creating a reminder.
- Inbox items older than seven days are automatically removed.
- Removal of the original notification from Android's notification shade does not remove its saved NotNow snapshot.
- Swipe gestures are out of scope; actions must be explicit and discoverable.

## 3. Create Reminder

### Presets

- `In 30 minutes`
- `In 1 hour`
- `Tonight`
- `Tomorrow morning`
- `Choose date & time`

### Time semantics

- Relative presets are calculated when the user confirms them.
- `Tonight` means 20:00 today, or 20:00 tomorrow if today's time has passed.
- `Tomorrow morning` means 09:00 tomorrow.
- A custom time cannot be in the past.
- Preset customization is out of scope for v0.1.

### Creation behavior

- Selecting a preset immediately creates the reminder.
- On success, the app shows a short confirmation and returns to Inbox.
- Android 13+ notification permission is requested in this context when the first reminder is created.
- If notification permission is denied, the reminder is not created because the app cannot reliably deliver it.

## 4. Reminder delivery

- The reminder notification contains the source app name and the original title and body.
- The notification exposes `Open`, `Remind Later`, and `Done` actions when available.
- Selecting the notification body opens Reminder Details in NotNow.
- `Done` marks the reminder Completed and dismisses its notification.
- `Remind Later` opens the time-selection screen; inline notification presets are out of scope.
- `Open` launches the source app on a best-effort basis.
- If the source app cannot be launched, `Open` is hidden.
- Opening the source app does not automatically complete the reminder.
- Dismissing the Android notification does not complete the reminder.
- A due reminder remains Overdue until explicitly completed or rescheduled.
- If the device was unavailable at the due time, the reminder is delivered at the first reasonable opportunity.

## 5. Reminder state model

```text
Scheduled ──due──> Overdue
    │                 │
    ├──done──────────> Completed
    │                 │
    └──reschedule────> Scheduled
```

Dismissal of the system notification is not a domain-state transition.

## 6. Reminders screen

- Active reminders are separated into `Overdue` and `Upcoming`.
- Within those sections, reminders are ordered by due time, nearest first.
- The visual time groups are `Overdue`, `Today`, `Tomorrow`, and `Later`.
- A row shows the source app, title, body preview, and reminder time.
- Selecting a row opens Reminder Details.
- Reminder Details offers `Open source app`, `Remind later`, and `Done` when applicable.
- There is no destructive delete action for active reminders; users complete them with `Done`.
- Completed reminders remain in storage but are not shown in v0.1.

### Empty state

```text
Nothing waiting for you

Turn a notification into a reminder
and handle it when you have time.
```

## 7. First-run onboarding

Onboarding has two steps.

### Product introduction

```text
Save it for later

Turn important notifications into reminders
and handle them when you have time.
```

### Notification Access explanation

```text
Allow notification access

NotNow needs notification access to show received
notifications in your private inbox.

Everything stays on your device.
```

The primary action is `Open Notification Access`; `Not now` is available as a secondary action.

### Access behavior

- The app explains the need before opening Android system settings.
- It rechecks the actual access state whenever the user returns to the app.
- Granted access leads to the normal Inbox.
- Without access, the app remains usable but Inbox shows an educational empty state and an enable-access action.
- Revoking access later does not delete existing local data.
- Revocation stops only the capture of new notifications; existing reminders continue working.
- `POST_NOTIFICATIONS` is not part of onboarding and is requested only while creating the first reminder.

## 8. Settings

The v0.1 Settings screen contains only essential controls.

```text
Permissions

Notification access        Enabled / Disabled
Reminder notifications     Enabled / Disabled

Data

Clear notification inbox

About

Privacy
Version
```

### Behavior

- `Notification access` opens Android's notification-listener access settings.
- `Reminder notifications` opens the app's notification settings on Android 13+; older versions show the current status.
- Permission states are rechecked whenever the screen resumes.
- `Clear notification inbox` removes only captured Inbox snapshots and never active reminders.
- Clearing requires an explicit confirmation dialog.
- `Privacy` is a short local page explaining that notification data does not leave the device.
- Theme, preset customization, app exclusions, and other advanced preferences are out of scope.

### Clear confirmation

```text
Clear notification inbox?

This removes all captured notifications that have not
been turned into reminders. Your reminders will not be affected.
```

## 9. UI states and failures

| State | User-visible behavior |
|---|---|
| Initial local load | Show a short progress indicator. |
| Empty Inbox with access | Show `No notifications yet`. |
| Empty Inbox without access | Explain the requirement and show `Enable access`. |
| Empty Reminders | Show `Nothing waiting for you`. |
| Reminder notification permission denied | Explain that reminders cannot be delivered and show `Open settings`. |
| Custom time is in the past | Show an inline error and disable confirmation. |
| Reminder creation fails | Keep the Inbox item and show `Couldn't create reminder`. |
| Alarm scheduling fails | Do not leave an active reminder; roll back the creation operation. |
| Source app is unavailable | Hide the `Open` action. |
| Body is empty | Show the source app and title only. |
| Title and body are both unusable | Do not capture the notification. |

Reminder creation is one user-visible operation: persistence and alarm scheduling must both succeed before success is shown. A failure must not remove the source Inbox item.

The UI displays the time selected by the user without promising exact-to-the-second delivery. The local Privacy/help copy explains that Android may slightly delay inexact alarms for battery efficiency.

## 10. Explicitly deferred

- Direct reminder creation from Android's notification shade
- Swipe actions
- Configurable time presets
- Inline rescheduling presets in the reminder notification
- Completed/history screen
- Search and advanced filtering
- Theme settings
- App exclusion settings

## 11. Reboot recovery

Reboot recovery is part of v0.1 because reminder delivery must remain reliable across device restarts.

```text
Device reboot
    ↓
BOOT_COMPLETED
    ↓
Read Scheduled reminders from Room
    ↓
Future reminder → schedule again
Past-due reminder → mark Overdue and notify
```

- The app declares `RECEIVE_BOOT_COMPLETED` and handles the system broadcast without opening foreground UI.
- Only active reminders are considered; Completed and Cancelled reminders are ignored.
- Future reminders are scheduled again.
- Past-due reminders become Overdue and are delivered immediately or at the first reasonable opportunity.
- Recovery is idempotent and must not create duplicate alarms or reminder notifications.
- Captured Inbox notifications are not reprocessed during recovery.
- The behavior is covered by tests and a documented manual verification scenario.

## 12. v0.1 UX completion criterion

The UX baseline is complete when all flows and states in this document can be mapped to explicit UI screens, domain transitions, and acceptance tests without inventing additional product behavior during implementation.
