# Bug Fix Report: HelpFragment Not Scrolling

## Problem Description

The `HelpFragment`, which contains a `ScrollView`, was not scrollable even when its content exceeded the screen height. This issue occurred on the main screen defined by `activity_main.xml`.

## Root Cause Analysis

The root cause of this issue was in the layout definition of `activity_main.xml`. The layout is structured as a `CoordinatorLayout` containing:
1.  An `AppBarLayout` (Toolbar) at the top.
2.  A `FrameLayout` (`@id/fragment_container`) for displaying fragments.
3.  A `BottomNavigationView` at the bottom.

The `FrameLayout` was configured with `android:layout_height="match_parent"`. In a `CoordinatorLayout`, this caused the frame to fill the entire space of its parent. While `app:layout_behavior="@string/appbar_scrolling_view_behavior"` correctly positioned it below the top `AppBarLayout`, there was no corresponding rule to position it *above* the `BottomNavigationView`.

As a result, the `FrameLayout` extended all the way to the bottom of the screen, and the `BottomNavigationView` was rendered on top of it. When `HelpFragment` was loaded into this `FrameLayout`, its `ScrollView` also filled this entire area. The content at the bottom of the `ScrollView` was therefore hidden behind the navigation bar, and since the `ScrollView`'s full height was never less than its content's height within the visible area, it did not scroll.

Changing the `ScrollView`'s height to `match_parent` in `fragment_help.xml` was the correct step for the fragment's layout, but the container it was placed in (`FrameLayout`) was the source of the problem.

## Solution

To fix this, we need to ensure the `FrameLayout` does not overlap with the `BottomNavigationView`. The solution is to add a bottom margin to the `FrameLayout` that is equal to the height of the `BottomNavigationView`.

The `BottomNavigationView` typically has a height equal to the standard action bar size. Therefore, the fix was to add the following attribute to the `FrameLayout` in `activity_main.xml`:

```xml
android:layout_marginBottom="?attr/actionBarSize"
```

This change resizes the `FrameLayout`, making it end just above the `BottomNavigationView`. This ensures that the entire area of the fragment is visible and that the `ScrollView` within `HelpFragment` functions correctly.

### File Modified
- `Android/app/src/main/res/layout/activity_main.xml`

## Bug Fix Report: TTS Unreliability and Fallback to iFlytek

### Problem Description

The reminder feature relied on the standard Android Text-to-Speech (TTS) engine. Logs and user reports indicated that on some devices (e.g., those with `com.vivo.aiservice` as the TTS engine), the default TTS would initialize but fail to speak, or silently disconnect, causing the user not to receive the audio reminder for their schedule. The service would simply stop without any audio output.

### Root Cause Analysis

The root cause is the inconsistency and unreliability of built-in TTS engines across different Android manufacturers and versions. Some OEM implementations of TTS services can be buggy or have non-standard behavior, leading to silent failures. Relying on a single TTS engine without a fallback mechanism makes the feature fragile. The log `Disconnected from TTS engine` after a long delay without any speech output is a strong indicator of this issue.

### Solution

To ensure the reliability of the audio reminders, a fallback system was implemented. The application now attempts to use the standard Android TTS first, but if it detects any failure, it automatically switches to the more robust iFlytek Online TTS service.

1.  **Failure Detection**: Failure points for the standard TTS were identified and handled in `ReminderService.java`:
    *   **Initialization Failure**: If the `onInit` callback returns a status other than `TextToSpeech.SUCCESS`.
    *   **Playback Error**: If the `tts.speak()` method returns `TextToSpeech.ERROR`.
    *   **Listener Error**: If the `UtteranceProgressListener`'s `onError` method is invoked.

2.  **iFlytek TTS Refactoring**: The iFlytek TTS logic, which was previously confined to `TTSActivity.java` and tied to the UI, was refactored.
    *   A new helper class, `IflytekTtsHelper.java`, was created to encapsulate all logic for iFlytek TTS synthesis and audio playback.
    *   This class is designed to run in the background, managing its own thread for audio playback via `AudioTrack`, making it suitable for use within a `Service`.
    *   It exposes a simple `speak(text, listener)` interface and uses a callback listener to report completion or errors.

3.  **Integration into `ReminderService`**:
    *   A new method, `triggerFallbackTts()`, was added to `ReminderService`.
    *   When any of the failure conditions for the standard TTS are met, this method is called.
    *   It safely shuts down the standard TTS engine, instantiates `IflytekTtsHelper`, and initiates speech using the iFlytek engine with the same reminder text.
    *   The service now waits for the callback from `IflytekTtsHelper` before stopping itself, ensuring the reminder is fully played.

This two-tiered approach significantly improves the robustness of the reminder feature. Users will now receive their audio alerts reliably, even if the default TTS engine on their device is faulty.

### Files Modified
- `Android/app/src/main/java/com/archive/app/reminders/ReminderService.java`
- `Android/doc/bugFix.md`

### Files Added
- `Android/app/src/main/java/com/archive/app/reminders/IflytekTtsHelper.java`

## Manual Testing via ADB

To test the reminder and TTS functionality without waiting for the actual alarm time, you can manually trigger the `ReminderBroadcastReceiver` using the Android Debug Bridge (adb). This directly starts the `ReminderService` and initiates the TTS playback.

### Prerequisites

You must have a valid schedule in the app's database. The command below uses `6` as the schedule ID. If a schedule with this ID does not exist, the service will start but won't have any text to speak. You can replace `6` with any valid schedule ID from your database.

### Steps

1.  Connect your device to your computer with USB debugging enabled.
2.  Open a terminal or command prompt.
3.  Execute the following command:

    ```bash
    adb shell am broadcast -n com.archive.app/com.archive.app.reminders.ReminderBroadcastReceiver --el extra_schedule_id 6
    
    
    adb -s 10CF4E0E41002E8 shell am broadcast -n com.archive.app/com.archive.app.reminders.ReminderBroadcastReceiver --el extra_schedule_id 6
    
    
    adb -s emulator-5554 shell am broadcast -n com.archive.app/com.archive.app.reminders.ReminderBroadcastReceiver --el extra_schedule_id 4
    ```

### Command Breakdown

-   `adb shell am broadcast`: The standard command to send a broadcast Intent.
-   `-n com.archive.app/com.archive.app.reminders.ReminderBroadcastReceiver`: The `-n` flag specifies an explicit component to receive the broadcast.
    -   `com.archive.app` is your application's package name.
    -   `/com.archive.app.reminders.ReminderBroadcastReceiver` is the fully qualified class name of the receiver.
-   `--el extra_schedule_id 6`: This adds extra data to the Intent.
    -   `--el`: Specifies that the extra data is a `long`.
    -   `extra_schedule_id`: This is the key for the extra data, which must match the constant `EXTRA_SCHEDULE_ID` used in the receiver.
    -   `6`: This is the value (the schedule ID) being passed.

After running the command, you should see the log "Received alarm for schedule ID: 6" in Logcat, and the device should begin to speak the reminder.
