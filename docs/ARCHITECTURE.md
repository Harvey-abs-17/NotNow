# NotNow Architecture

Status: Approved baseline for v0.1

## 1. Architectural style

NotNow uses a pragmatic Clean Architecture adaptation for Android with three primary layers:

```text
Presentation → Domain ← Data
```

The dependency rule is more important than folder names: source-code dependencies point toward the domain. Android framework and persistence details stay outside it.

The project also uses feature-oriented presentation modules and a Gradle included build for shared build conventions.

## 2. Project structure

```text
NotNow
├── app
├── build-logic
│   └── convention
│
├── core
│   ├── designsystem
│   ├── ui
│   └── testing
│
├── domain
├── data
│
└── feature
    ├── onboarding
    ├── inbox
    ├── reminders
    └── settings
```

Not every planned module must be created immediately. A module is introduced when its first real responsibility is implemented.

## 3. Dependency graph

```text
                         ┌──────────────────┐
                         │       app        │
                         └───────┬──────────┘
                                 │ composes
               ┌─────────────────┼─────────────────┐
               ▼                 ▼                 ▼
        feature modules         data             domain
               │                 │                 ▲
               ├─────────────────┼─────────────────┘
               │                 │ implements ports
               ▼                 ▼
          core:ui        core:designsystem
               │
               ▼
       core:designsystem
```

Allowed primary dependencies:

```text
:app                 → :feature:*, :data, :domain
:feature:*           → :domain, :core:ui, :core:designsystem
:data                → :domain
:core:ui             → :core:designsystem
:core:testing        → modules needed only for shared test utilities
:domain              → Kotlin and explicitly approved foundational libraries only
```

Forbidden dependencies:

```text
:domain              ✕ Android SDK, Compose, Room, ViewModel, AlarmManager
:domain              ✕ :data, :feature:*, :app
:data                ✕ :feature:*, :app
:feature:*           ✕ :data
:feature:*           ✕ another feature implementation
```

Cross-feature navigation is coordinated by `:app`; features expose routes and callbacks without directly depending on each other.

## 4. Module responsibilities

### `:domain`

A Kotlin/JVM module containing product rules and abstractions.

```text
model/
repository/
scheduler/
usecase/
```

Initial concepts include:

- `NotificationSnapshot`
- `Reminder`
- `ReminderStatus`
- `NotificationRepository`
- `ReminderRepository`
- `ReminderScheduler`
- notification capture, reminder creation, completion, rescheduling, observation, and reboot-recovery use cases

A `Reminder` keeps the source notification identifier for traceability and also owns a copy of the source app name, package name, title, and body. This keeps reminders useful after their originating inbox snapshot is removed and prevents the snapshot lifecycle from controlling the reminder lifecycle.

Use cases are created for meaningful application behavior. The project does not add pass-through use cases merely to satisfy a naming pattern.

### `:data`

An Android library containing implementations of domain ports and external details:

```text
database/
repository/
mapper/
scheduler/
notification/
```

It owns Room entities, DAOs, migrations, repository implementations, Android-backed scheduler implementations, and mapping between external/storage types and domain models.

Room remains inside `:data` for v0.1. It may become `:data:database` only when size or reuse creates a concrete reason.

### `:feature:*`

Android presentation modules organized by user-facing capability. A typical feature owns:

```text
FeatureRoute
FeatureScreen
FeatureViewModel
FeatureUiState
FeatureAction
```

Features consume domain APIs and expose user intent. They do not know persistence or Android scheduling implementations.

`Create Reminder` and `Reminder Details` belong to `:feature:reminders`; separate Gradle modules are not justified for those screens in v0.1.

### `:core:designsystem`

Owns the NotNow theme, typography, colors, icons, and primitive reusable components. It does not contain domain-aware UI.

### `:core:ui`

Owns reusable composite UI and UI utilities shared by multiple features. It is introduced only when shared UI appears; feature-specific composables remain in their feature.

### `:core:testing`

Owns shared fakes, fixtures, coroutine test rules, and other test-only utilities. Production code must not depend on it.

### `:app`

The application composition root. It owns:

- `Application` and `MainActivity`
- root navigation
- manifest declarations
- dependency-injection composition
- Android entry points such as notification listener and broadcast receivers

Framework entry points remain thin and delegate immediately to domain behavior. They do not contain product rules.

## 5. Data and event flow

Reads are reactive and expose `Flow` from repository boundaries:

```text
Room → Data repository → Domain API → ViewModel → UiState → Compose
```

User events flow in the opposite direction:

```text
Compose action → ViewModel → Use case/repository port → Data implementation
```

Room is the local source of truth for captured notifications and reminders.

## 6. Dependency injection

Hilt is used to connect outer-layer implementations to domain interfaces and to inject Android entry points and ViewModels.

Bindings belong as close as practical to their implementation. `:app` remains the final composition root and must not accumulate business logic.

## 7. Build logic

Shared Gradle configuration lives in an included `build-logic` build, not `buildSrc` or root `subprojects` blocks.

Planned convention plugins:

```text
notnow.android.application
notnow.android.library
notnow.android.library.compose
notnow.android.feature
notnow.android.hilt
notnow.android.room
notnow.kotlin.library
```

Conventions are additive and single-purpose. One-off configuration stays in the consuming module rather than becoming a global convention.

The version catalog owns dependency and plugin coordinates; convention plugins own shared configuration.

## 8. Testing boundaries

- Domain entities and use cases receive fast JVM unit tests.
- Data repositories and mappers receive JVM tests where possible and database integration tests where required.
- ViewModels are tested through state and event behavior.
- Compose tests cover critical user flows rather than implementation details.
- Alarm, notification-access, permission, process-death, and reboot behavior also have documented device/emulator verification scenarios.

## 9. Module creation rule

A new module requires at least one concrete benefit:

- enforcing an otherwise fragile dependency boundary;
- isolating a reusable responsibility;
- enabling independent testing or build behavior;
- separating code with a distinct lifecycle or platform concern.

Module count is not a quality metric. Architectural boundaries and correct dependency direction are.

## 10. Decision records

Any intentional deviation from this document must be captured in an Architecture Decision Record before the dependency graph is changed.
