# Tarneeb Score Tracker

A complete native Android scorekeeper for two-team Tarneeb games. The app is built with Kotlin, Jetpack Compose, Material 3, MVVM, and Preferences DataStore.

## Included features

- First-run setup with team names and 31, 41, 61, or custom targets
- Large responsive scoreboard with leader highlighting and dark mode support
- Validated bid/trick entry with a live score preview
- Exact Jawaker-style normal, all-tricks, and bid-13 scoring
- Detailed round history with notes, per-team deltas, and cumulative totals
- Edit, delete, and undo with full replay from round one
- Game-over winner/tie handling, same-team restart, new-team restart, and continue-anyway mode
- Automatic local persistence of the current game and complete history
- Unit tests for all required scoring cases and history recalculation

## Scoring

All scoring is implemented in `TarneebScoring.kt` and nowhere in the UI.

| Situation | Bidding team | Opposing team |
| --- | ---: | ---: |
| Bid succeeds normally | Tricks actually won | 0 |
| Bid fails normally | −bid | Opposing tricks |
| Wins all 13 without bidding 13 | +16 | 0 |
| Bids 13 and wins all 13 | +26 | 0 |
| Bids 13 and fails | −16 | Opposing tricks |

Scores may be negative. The game reaches game over when either total is at least the target. If both totals reach the target together, the higher total wins; equal totals produce a tie.

## Main project files

- `app/src/main/java/com/qusai/tarneebscoretracker/scoring/TarneebScoring.kt` — pure round scoring function
- `app/src/main/java/com/qusai/tarneebscoretracker/scoring/GameCalculator.kt` — replays history and calculates every cumulative total
- `app/src/main/java/com/qusai/tarneebscoretracker/model/` — serializable game, team, round, and scoring models
- `app/src/main/java/com/qusai/tarneebscoretracker/data/` — DataStore-backed local storage and repository
- `app/src/main/java/com/qusai/tarneebscoretracker/viewmodel/GameViewModel.kt` — state management and game actions
- `app/src/main/java/com/qusai/tarneebscoretracker/ui/` — Compose setup, scoreboard, history, dialogs, and reusable components
- `app/src/test/java/com/qusai/tarneebscoretracker/scoring/` — scoring and replay unit tests

## Open in Android Studio

1. Install the current stable [Android Studio](https://developer.android.com/studio).
2. Start Android Studio and choose **Open**.
3. Select the `Tarneeb-Codex` folder containing this README and `settings.gradle.kts`.
4. Let Gradle sync. If prompted, install Android SDK Platform 35 and Build Tools 35.0.0.
5. Android Studio includes a suitable JDK; this project targets Java 17.

`local.properties` is machine-specific and normally created or updated automatically by Android Studio. If the checked-in working copy points at a missing SDK, use **File > Project Structure > SDK Location** to select your Android SDK.

## Run on an emulator

1. In Android Studio, open **Tools > Device Manager**.
2. Choose **Create device**, select a phone profile, and install/select an Android system image.
3. Start the virtual device.
4. Select the `app` run configuration and the emulator, then click **Run**.

## Run on an Android phone with USB debugging

1. On the phone, open **Settings > About phone** and tap **Build number** seven times to enable Developer options. The exact menu name can vary by manufacturer.
2. Open **Developer options** and enable **USB debugging**.
3. Connect the phone by USB, choose a data-capable USB mode, and accept the debugging fingerprint prompt on the phone.
4. Select the phone in Android Studio's device selector and click **Run**.

You can verify the connection from a terminal with:

```powershell
adb devices
```

## Build and install an APK

To create an installable debug APK in Android Studio, choose **Build > Build App Bundle(s) / APK(s) > Build APK(s)**. The file is generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

The same build from PowerShell is:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

Install the APK by transferring it to the phone and opening it (allow installation from that source if Android asks), or install over USB:

```powershell
adb install -r .\app\build\outputs\apk\debug\app-debug.apk
```

An already-built debug APK is also placed in `dist/Tarneeb-Score-Tracker-debug.apk` in this delivery.
For transfer to another computer, `dist/Tarneeb-Score-Tracker-Android-Studio-Project.zip` contains the clean source project and Gradle wrapper without machine-specific SDK settings or build caches.

## Create a signed release APK

1. In Android Studio, choose **Build > Generate Signed App Bundle or APK**.
2. Select **APK**, then choose or create a keystore.
3. Select the `release` build variant and finish the wizard.
4. Android Studio shows the exact output location when signing completes; Gradle release outputs are normally under `app/build/outputs/apk/release/`.

Keep the keystore and its passwords backed up. Future updates must be signed with the same key. The debug APK is convenient for personal installation, while a signed release APK is appropriate for long-term distribution.

## Verification

Run all local unit tests and build both variants with:

```powershell
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug assembleRelease
```

The tests cover every scoring example in the specification, invalid ranges, history editing, deletion, undo, game-over/continue behavior, and serialized state round-tripping.

## Assumptions

- A game has exactly two teams and 13 tricks per round.
- If one team name is left blank, it falls back to `Team 1` or `Team 2`; both names cannot be blank.
- Rounds are displayed newest-first, while their permanent round numbers and replay order remain chronological.
- Continuing after game over keeps accepting rounds. Editing or deleting history always recomputes the complete score and game-over state.
- Current game data is stored only on the device in the app's private DataStore storage. No account, network permission, or cloud service is required.
