این را به‌عنوان Project Brief اولیه نگه دار. بعداً می‌توانیم از روی همین سند وارد طراحی UX، معماری و در نهایت پیاده‌سازی شویم.

# Notification → Reminder

## Project Brief — نسخه اولیه

### 1. معرفی پروژه

`Notification → Reminder` یک ابزار Android است که به کاربر اجازه می‌دهد یک Notification دریافتی از هر اپ را به یک Reminder تبدیل کند.

مسئله‌ای که پروژه حل می‌کند بسیار ساده است: بسیاری از Notificationها در زمانی می‌رسند که کاربر امکان رسیدگی به آن‌ها را ندارد. در این شرایط معمولاً یکی از سه اتفاق می‌افتد:

* Notification در پنل باقی می‌ماند و باعث شلوغی می‌شود.
* کاربر آن را dismiss می‌کند و بعداً فراموش می‌کند.
* کاربر آن را می‌بیند و امیدوار است بعداً یادش بماند.

هدف پروژه ایجاد یک مرحله بین «دریافت Notification» و «رسیدگی به آن» است:

```text
Notification received
        ↓
Not now
        ↓
Remind me later
        ↓
Reminder
        ↓
Done
```

بنابراین این پروژه یک Reminder App عمومی نیست؛ Reminderها از context واقعی Notificationهای سایر اپ‌ها ساخته می‌شوند.

---

## 2. مثال واقعی

فرض کنیم GitHub این Notification را ارسال کرده است:

```text
GitHub

Review requested
Hasan requested your review on PR #428
```

کاربر در حال حاضر نمی‌تواند PR را بررسی کند.

در اپ ما Notification را می‌بیند و انتخاب می‌کند:

```text
Remind me

30 minutes
1 hour
Tonight
Tomorrow
Choose time...
```

مثلاً:

```text
Tomorrow — 09:00
```

فردا اپ این Reminder را نشان می‌دهد:

```text
GitHub

Review requested
Hasan requested your review on PR #428

[ Open GitHub ]
[ Remind Later ]
[ Done ]
```

این workflow هسته‌ی محصول است.

---

# 3. ارزش اصلی محصول

ارزش اصلی پروژه در این نیست که Notificationها را ذخیره کند.

ارزش اصلی این است که Notification را از یک event موقتی به یک task قابل رسیدگی تبدیل کند.

مدل ذهنی پروژه:

```text
Notification
    ↓
Actionable item
    ↓
Scheduled reminder
    ↓
Resolution
```

به همین دلیل حتی اسم محصول در آینده بهتر است حول مفاهیمی مثل:

```text
Later
Remind
FollowUp
NotNow
PingBack
NotifyLater
```

باشد، نه چیزی مثل `NotificationHistory`.

اسم نهایی فعلاً اهمیتی ندارد.

---

# 4. کاربران هدف

نسخه اول می‌تواند برای عموم کاربران ساخته شود، ولی چند گروه بیشترین استفاده را خواهند داشت:

### کاربران پرNotification

افرادی که از Telegram، Gmail، Slack، GitHub، Discord و اپ‌های مشابه Notification زیادی دریافت می‌کنند.

### Developerها

مثلاً:

```text
GitHub
Review requested

Jira
Issue assigned

CI
Build failed
```

Notificationهایی که باید بعداً به آن‌ها رسیدگی شود.

### کاربران کاری

مثلاً:

```text
Gmail
Invoice received

Slack
Can you review this?

Calendar
Document was updated
```

محصول نباید مخصوص Developerها باشد، ولی Developerها می‌توانند یکی از use caseهای بسیار خوب آن باشند.

---

# 5. تجربه کاربری اصلی

نسخه اول سه جریان اصلی دارد.

## Flow A — دریافت Notification

Android یک Notification جدید دریافت می‌کند.

`NotificationListenerService` اپ ما event مربوط به posted/removed notificationها را دریافت می‌کند. این سرویس API رسمی Android برای مشاهده تغییرات Notificationها است. ([Android Developers][1])

ما فقط اطلاعات موردنیاز را استخراج می‌کنیم.

مثلاً:

```text
Source App
GitHub

Title
Review requested

Body
Hasan requested your review on PR #428

Received At
14:32
```

و یک snapshot محلی ایجاد می‌کنیم.

---

## Flow B — تبدیل به Reminder

کاربر Notification را انتخاب می‌کند.

صفحه یا Bottom Sheet ساده‌ای نمایش داده می‌شود:

```text
Remind me

○ In 30 minutes
○ In 1 hour
○ Tonight
○ Tomorrow morning
○ Choose date & time
```

پس از انتخاب:

```text
Reminder created
Tomorrow at 09:00
```

Notification دیگر از نظر محصول یک «Notification» نیست؛ تبدیل به یک `Reminder` شده است.

این distinction در domain model مهم است.

---

## Flow C — رسیدن زمان Reminder

در زمان تعیین‌شده:

```text
GitHub

Review requested
Hasan requested your review on PR #428
```

actions:

```text
Open
Remind Later
Done
```

`Done` یعنی task برای کاربر تمام شده است.

`Remind Later` یعنی schedule جدید.

`Open` باید تا جای ممکن کاربر را به اپ مبدأ برگرداند.

---

# 6. MVP

نسخه `v0.1` باید بسیار محدود بماند.

تنها قابلیت‌های ضروری:

```text
Notification Access

        ↓

Capture notifications

        ↓

Notification Inbox

        ↓

Create Reminder

        ↓

Schedule Reminder

        ↓

Show Notification

        ↓

Done / Remind Later
```

### قابلیت‌های v0.1

* دریافت Notificationهای جدید
* نمایش نام و icon اپ مبدأ
* نمایش title
* نمایش body
* نمایش زمان دریافت
* ساخت Reminder
* presetهای زمانی
* انتخاب date/time دلخواه
* ذخیره Reminderها
* نمایش Reminder در زمان مشخص
* Done
* Remind Later
* حذف Notificationهای قدیمی از inbox
* مدیریت وضعیت Notification Access

همین.

---

# 7. چیزهایی که عمداً در v0.1 نمی‌سازیم

این قسمت بسیار مهم است.

در نسخه اول نمی‌خواهیم:

```text
AI
Notification summarization
Cloud sync
Account
Login
Cross-device sync
Automatic classification
Smart rules
Automatic reminders
Notification analytics
Complex filtering
Search
Backup
Web dashboard
```

همچنین در v0.1 نیازی به:

```text
multiple modules everywhere
over-engineered Clean Architecture
complex plugin system
```

نداریم.

هدف نسخه اول:

> یک workflow کوچک ولی کاملاً قابل‌اعتماد.

---

# 8. صفحات اصلی

به نظرم نسخه اول فقط به چهار surface اصلی نیاز دارد.

## Inbox

```text
Later

Notifications

GitHub
Review requested on PR #428
2 min ago

Gmail
Your invoice is ready
12 min ago

Telegram
Hasan sent a document
25 min ago
```

---

## Create Reminder

```text
GitHub

Review requested
Hasan requested your review...

────────────────────

Remind me

30 minutes
1 hour
Tonight
Tomorrow
Custom
```

---

## Reminders

```text
Upcoming

TODAY

18:00
Gmail
Your invoice is ready


TOMORROW

09:00
GitHub
Review requested on PR #428
```

---

## Settings

در نسخه اول تقریباً فقط:

```text
Notification access       ✓
Reminder notifications    ✓

About
Privacy
```

---

# 9. مدل داده پیشنهادی

نباید `StatusBarNotification` یا Android framework objectها را مستقیم وارد domain/storage کنیم.

یک snapshot مستقل می‌خواهیم.

مثلاً:

```kotlin
data class NotificationSnapshot(
    val id: String,
    val sourcePackage: String,
    val sourceAppName: String,
    val title: String?,
    val body: String?,
    val postedAt: Instant
)
```

و Reminder:

```kotlin
data class Reminder(
    val id: String,
    val sourceNotificationId: String?,
    val sourcePackage: String,
    val title: String?,
    val body: String?,
    val remindAt: Instant,
    val status: ReminderStatus
)
```

با:

```kotlin
enum class ReminderStatus {
    Scheduled,
    Completed,
    Cancelled
}
```

این separation باعث می‌شود lifecycle نوتیفیکیشن مبدأ، lifecycle Reminder ما را کنترل نکند.

---

# 10. تصمیم مهم درباره PendingIntent

بعضی Notificationها یک `contentIntent` دارند که مثلاً کاربر را مستقیماً به conversation یا PR مربوطه می‌برد.

در نگاه اول ممکن است وسوسه شویم آن را ذخیره کنیم و روز بعد دوباره اجرا کنیم.

نباید معماری محصول را روی این فرض بنا کنیم.

`PendingIntent` متعلق به اپ مبدأ و lifecycle سیستم است و نباید آن را مانند یک identifier پایدار در دیتابیس در نظر بگیریم.

بنابراین رفتار `Open` باید best-effort باشد.

ترتیب منطقی می‌تواند چنین باشد:

```text
Original destination available?
        ↓ yes
Open destination
        ↓ no

Can launch source application?
        ↓ yes
Open source app
        ↓ no

Hide Open action
```

در نتیجه یکی از اصول پروژه:

> Reminder باید حتی بدون امکان بازکردن مقصد اصلی کاملاً مفید باشد.

یعنی title/body خود Reminder باید context کافی داشته باشند.

---

# 11. زمان‌بندی Reminder

این یکی از تصمیم‌های مهم معماری است.

برای Reminderهایی که کاربر برای یک زمان آینده تعیین کرده، `WorkManager` الزاماً ابزار مناسبی برای تحویل دقیق در یک ساعت مشخص نیست.

Android برای actionهایی که کاربر درخواست کرده پس از زمان مشخص اجرا شوند، استفاده از Alarmها را پشتیبانی می‌کند. مستندات Android برای بسیاری از این use caseها ابتدا inexact alarm را توصیه می‌کنند؛ exact alarm باید تنها وقتی استفاده شود که precision واقعاً بخش اصلی functionality باشد. ([Android Developers][2])

برای `v0.1` پیشنهاد من:

```text
AlarmManager
+
inexact alarm
```

بدون درخواست `SCHEDULE_EXACT_ALARM`.

مثلاً کاربر می‌گوید:

```text
Tomorrow morning
```

و نیازی نداریم دقیقاً در:

```text
09:00:00.000
```

اجرا شود.

این تصمیم مزایای مهمی دارد:

* permission اضافی نداریم.
* Play Store policy ساده‌تر می‌شود.
* battery impact کمتر است.
* implementation ساده‌تر می‌ماند.

اگر بعداً کاربران واقعاً precision دقیقه‌ای خواستند، exact scheduling را جداگانه بررسی می‌کنیم.

Android برای exact alarms در Android 12+ special access/permission جداگانه دارد و توصیه می‌کند فقط در use caseهای واقعاً time-critical استفاده شود. ([Android Developers][2])

---

# 12. Permissionها و Special Access

دو دسترسی اصلی داریم.

### Notification Access

برای `NotificationListenerService`.

کاربر باید Notification Access را از تنظیمات سیستم به برنامه بدهد.

این مهم‌ترین permission محصول است، چون بدون آن core functionality وجود ندارد.

Onboarding باید دقیقاً توضیح دهد:

```text
Why do we need notification access?

We use notification access only to let you
turn selected notifications into reminders.

Your notification content stays on your device.
```

نه اینکه مستقیم کاربر را بدون explanation وارد Settings کنیم.

---

### POST_NOTIFICATIONS

برای اینکه خود برنامه بتواند Reminder notification نمایش دهد.

از Android 13 به بعد `POST_NOTIFICATIONS` runtime permission است. ([Android Developers][3])

این permission را بهتر است زمانی درخواست کنیم که کاربر اولین Reminder را می‌سازد، نه بلافاصله هنگام launch برنامه.

یعنی permission در context واقعی درخواست شود.

---

# 13. Privacy

این پروژه از نظر privacy حساس‌تر از یک اپ معمولی است، چون Notificationها ممکن است شامل:

```text
private messages
email subjects
financial information
verification codes
work information
personal data
```

باشند.

بنابراین privacy باید بخشی از design باشد، نه یک README note.

برای نسخه اول پیشنهاد می‌کنم:

```text
100% local
No backend
No analytics containing notification content
No account
No cloud sync
No notification text in crash logs
```

Google Play هم برای APIها و permissionهایی که به داده حساس دسترسی دارند تأکید می‌کند access باید مستقیماً برای functionality اعلام‌شده برنامه ضروری باشد، به کاربر توضیح داده شود و فقط برای همان هدف استفاده شود. ([Google Help][4])

این حتی می‌تواند یکی از selling pointهای پروژه باشد:

```text
Private by design.
Your notifications never leave your device.
```

---

# 14. معماری پیشنهادی

برای نسخه اول معماری را ساده نگه می‌داریم.

تقریباً:

```text
NotificationListenerService
        ↓
NotificationRepository
        ↓
Room
        ↓
UI
```

و:

```text
Create Reminder
        ↓
ReminderRepository
        ↓
Room
        ↓
ReminderScheduler
        ↓
AlarmManager
        ↓
ReminderReceiver
        ↓
NotificationManager
```

ساختار احتمالی:

```text
app

├── notification
│   ├── listener
│   ├── mapper
│   └── repository
│
├── reminder
│   ├── scheduler
│   ├── repository
│   └── model
│
├── data
│   └── database
│
└── ui
    ├── inbox
    ├── reminders
    └── settings
```

فعلاً multi-module ضروری نیست.

اگر پروژه بعداً بزرگ شد می‌توانیم module boundaries واقعی تعریف کنیم.

---

# 15. تکنولوژی‌های مناسب

پشته فنی اولیه:

```text
Kotlin
Jetpack Compose
Coroutines
Flow
Room
NotificationListenerService
AlarmManager
BroadcastReceiver
NotificationManager
Navigation Compose
Material 3
```

اختیاری:

```text
Hilt
```

اگر dependency injection واقعاً به تمیزی پروژه کمک کند.

هدف این نیست که هر library محبوب Android را وارد repository کنیم.

---

# 16. چالش‌های مهندسی جذاب پروژه

همین پروژه کوچک چند مسئله واقعی Android دارد.

### Process death

Reminder باید بعد از بسته‌شدن کامل اپ همچنان کار کند.

### Device reboot

Alarmهای معمولی پس از reboot باید دوباره schedule شوند.

بعداً باید چیزی مثل:

```text
BOOT_COMPLETED
        ↓
load scheduled reminders
        ↓
reschedule
```

داشته باشیم.

این را می‌توان در `v0.1` یا `v0.2` بر اساس scope نهایی قرار داد.

### Notification updates

یک Notification ممکن است با همان identity update شود.

نباید هر update را Notification جدید حساب کنیم.

### Ongoing notifications

مثل:

```text
Media playback
VPN
Downloads
Navigation
```

احتمالاً نباید inbox را با آن‌ها پر کنیم.

### Grouped notifications

یک app ممکن است summary و child notifications داشته باشد.

### Empty/poor notification content

بعضی Notificationها title/body مناسبی ندارند.

### Duplicate reminders

باید مشخص کنیم یک Notification می‌تواند چند Reminder همزمان داشته باشد یا نه.

برای `v0.1` پیشنهاد من:

> هر Notification فقط یک Reminder فعال.

---

# 17. Notification filtering در نسخه اولیه

Inbox نباید dump خام تمام Notificationهای Android باشد.

از ابتدا بهتر است موارد واضح را حذف کنیم، مثلاً:

```text
our own notifications
ongoing system notifications
group summaries where appropriate
notifications without useful content
```

اما نباید وارد smart classification شویم.

فیلتر باید deterministic و قابل‌فهم باشد.

---

# 18. Roadmap

### v0.1 — Core

```text
Notification capture
Inbox
Create reminder
Schedule
Reminder notification
Done
Remind later
```

### v0.2 — Reliability

```text
Reboot rescheduling
Better filtering
Notification update handling
App exclusions
Improved error states
```

### v0.3 — Workflow

```text
History
Search
Pinned reminders
Custom presets
Open source app
```

### v0.4 — Rules

مثلاً:

```text
GitHub
→ default: tomorrow 09:00

Slack
→ default: 1 hour

Shopping apps
→ ignore
```

### نسخه‌های بعدی

فقط در صورت وجود نیاز واقعی:

```text
Notification categories
Automatic grouping
Digest
Cross-device sync
Smart suggestions
```

هیچ‌کدام برای شروع ضروری نیست.

---

# 19. چیزی که پروژه را در GitHub قوی می‌کند

ارزش repository فقط کد نیست.

README نهایی باید بلافاصله مسئله را نشان دهد:

```text
Never lose an actionable notification again.

Turn any notification into a reminder
and deal with it when you actually have time.
```

بعد یک GIF:

```text
GitHub notification
       ↓
Remind tomorrow
       ↓
Tomorrow
       ↓
Reminder
       ↓
Done
```

و سپس architecture مختصر.

برای repository خوب همچنین لازم است:

```text
Screenshots
Demo GIF
Architecture overview
Tests
CI
Contributing guide
License
Release notes
```

اما این‌ها بعد از کارکردن محصول اضافه می‌شوند، نه قبل از آن.

---

# 20. معیار موفقیت v0.1

نسخه اول وقتی تمام شده محسوب می‌شود که این scenario بدون مشکل کار کند:

```text
1. User installs app.

2. User grants Notification Access.

3. Gmail sends a notification.

4. App captures it.

5. User sees it in Inbox.

6. User chooses:
   Remind me in 1 hour.

7. User completely closes the app.

8. One hour later reminder appears.

9. User selects Done.

10. Reminder disappears from Upcoming
    and becomes completed.
```

اگر این workflow reliable باشد، `v0.1` موفق است.

هر feature دیگری bonus است.

---

# 21. اصل طراحی پروژه

سه اصل باید در طول implementation ثابت بماند:

### Local-first

Notification content به‌طور پیش‌فرض از device خارج نمی‌شود.

### Reliable before smart

اول Reminder باید همیشه درست کار کند؛ بعد سراغ featureهای هوشمند می‌رویم.

### Small core

محصول اصلی باید همیشه قابل توضیح در یک جمله باقی بماند:

> Turn a notification into a reminder and handle it later.

اگر feature جدیدی این هسته را شلوغ کند، احتمالاً به نسخه فعلی تعلق ندارد.

---

# جمع‌بندی

`Notification → Reminder` پروژه مناسبی برای شروع portfolio است، چون در نقطه خوبی بین «اپ ساده» و «پروژه بیش‌ازحد بزرگ» قرار دارد.

از یک طرف scope اولیه آن محدود است:

```text
Notification
→ Reminder
→ Later
```

از طرف دیگر چند مفهوم واقعی Android را مجبور می‌شوی درست حل کنی:

```text
NotificationListenerService
special access
background execution
AlarmManager
BroadcastReceiver
process death
reboot
Room persistence
runtime notification permission
system notifications
privacy
```

بنابراین خروجی نهایی نه یک CRUD معمولی است، نه یک پروژه‌ای که ماه‌ها طول بکشد تا اولین نسخه‌اش قابل استفاده شود.

برای شروع implementation، مرز مناسب همان `v0.1` است:

**Capture → Inbox → Schedule → Remind → Done**

قدم منطقی بعدی به نظرم کدنویسی نیست؛ باید قبلش دقیقاً **UX و رفتار `v0.1`** را مشخص کنیم: چه Notificationهایی وارد Inbox شوند، کاربر از کجا Reminder می‌سازد، چه presetهایی داشته باشیم و stateهای Inbox/Reminder دقیقاً چه باشند. بعد معماری را روی همان رفتار طراحی می‌کنیم.

[1]: https://developer.android.com/reference/android/service/notification/NotificationListenerService.html?utm_source=chatgpt.com "NotificationListenerService  |  API reference  |  Android Developers"
[2]: https://developer.android.com/develop/background-work/services/alarms?utm_source=chatgpt.com "Schedule alarms  |  Background work  |  Android Developers"
[3]: https://developer.android.com/develop/ui/compose/notifications/notification-permission?utm_source=chatgpt.com "Notification runtime permission  |  Jetpack Compose  |  Android Developers"
[4]: https://support.google.com/googleplay/android-developer/answer/16558241?hl=en&utm_source=chatgpt.com "Permissions and APIs that Access Sensitive Information - Play Console Help"

