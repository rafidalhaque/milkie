# Daily Milk Tracker

A simple Android app to track daily milk deliveries with monthly summaries and cost calculations.

## Features

- **Daily Entry**: Record how much milk you received each day (0L, 0.5L, 1L, or custom amount)
- **Monthly Summary**: View total liters collected and total cost for any month
- **Daily Notifications**: Get reminded at your preferred time to log your milk delivery
- **Quick Actions**: Log milk amounts directly from notifications (0L, 0.5L, 1L)
- **Customizable Settings**: Set notification time and price per liter
- **Local Storage**: All data stored locally using Room (SQLite) database

## Screenshots

The app includes three main screens:
- **Home Screen**: Log today's milk delivery with quick select buttons
- **Monthly Summary**: View detailed breakdown of milk received and costs
- **Settings**: Configure notification time and price per liter

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: Single-activity with Compose Navigation
- **Database**: Room (SQLite)
- **Preferences**: DataStore
- **Notifications**: AlarmManager + NotificationCompat

## Project Structure

```
app/
├── src/main/
│   ├── java/com/milktracker/dailymilk/
│   │   ├── data/
│   │   │   ├── MilkEntry.kt          # Database entity
│   │   │   ├── MilkEntryDao.kt       # Data Access Object
│   │   │   ├── MilkDatabase.kt       # Room database
│   │   │   ├── MilkRepository.kt     # Data repository
│   │   │   └── PreferencesRepository.kt  # Settings storage
│   │   ├── notification/
│   │   │   ├── NotificationHelper.kt      # Notification management
│   │   │   ├── NotificationReceiver.kt    # Alarm receiver
│   │   │   ├── NotificationActionReceiver.kt  # Quick action handler
│   │   │   └── BootReceiver.kt            # Reschedule on boot
│   │   ├── ui/
│   │   │   ├── navigation/
│   │   │   │   ├── Screen.kt         # Navigation routes
│   │   │   │   └── NavGraph.kt       # Navigation graph
│   │   │   ├── screens/
│   │   │   │   ├── HomeScreen.kt     # Daily entry screen
│   │   │   │   ├── MonthlySummaryScreen.kt  # Monthly overview
│   │   │   │   └── SettingsScreen.kt # Settings screen
│   │   │   └── theme/
│   │   │       ├── Color.kt          # Color definitions
│   │   │       ├── Theme.kt          # App theme
│   │   │       └── Typography.kt     # Typography styles
│   │   ├── MainActivity.kt           # Main activity
│   │   └── MilkTrackerApplication.kt # Application class
│   ├── res/
│   │   ├── values/                   # Resources (strings, colors, themes)
│   │   ├── drawable/                 # Icons and drawables
│   │   ├── mipmap-*/                 # App icons
│   │   └── xml/                      # Backup rules
│   └── AndroidManifest.xml           # App manifest
└── build.gradle.kts                  # App build configuration
```

## Requirements

- Android SDK 26+ (Android 8.0 Oreo)
- Android Studio Hedgehog or newer
- Kotlin 1.9.20+

## Building the App

1. Clone the repository:
   ```bash
   git clone https://github.com/rafidalhaque/milkie.git
   cd milkie
   ```

2. Open in Android Studio or build from command line:
   ```bash
   ./gradlew assembleDebug
   ```

3. Install on device/emulator:
   ```bash
   ./gradlew installDebug
   ```

## Permissions

The app requires the following permissions:
- `POST_NOTIFICATIONS` - To send daily reminders
- `SCHEDULE_EXACT_ALARM` - To schedule notifications at exact times
- `RECEIVE_BOOT_COMPLETED` - To reschedule notifications after device restart
- `VIBRATE` - For notification vibration
- `WAKE_LOCK` - To ensure notifications are delivered

## License

MIT License - see [LICENSE](LICENSE) file for details