
# 日程语音提醒功能设计方案

## 1. 概述

本方案旨在为语音日程助手应用设计并实现一个日程提醒功能。该功能会在用户设定的日程即将开始前，通过语音播报的方式提醒用户，确保用户不会错过重要安排。即使应用处于后台或设备重启，提醒功能也应能正常工作。

## 2. 核心组件

为了实现稳定、可靠且省电的提醒功能，我们将依赖以下几个 Android 系统的核心组件：

-   **`AlarmManager` (闹钟管理器)**: Android 系统服务，用于在未来的某个精确时间点执行操作。这是实现定时任务的最佳选择，因为它能够在应用未运行时唤醒设备来触发事件。
-   **`BroadcastReceiver` (广播接收器)**: 用于接收由 `AlarmManager` 在指定时间发出的系统级广播。这是连接定时事件和应用逻辑的桥梁。
-   **`Service` (服务)**: 一个在后台执行长时间操作的组件。在本项目中，它将负责处理具体的提醒逻辑，如查询数据库和调用语音引擎，以避免在 `BroadcastReceiver` 中执行耗时操作。
-   **`TextToSpeech (TTS)` (文本转语音)**: Android 内置的语音合成引擎，可以将指定的文本字符串转换为语音并播放出来。
-   **`BroadcastReceiver` (开机广播接收器)**: 用于监听 `ACTION_BOOT_COMPLETED` 广播。因为 `AlarmManager` 设置的闹钟在设备重启后会失效，我们需要通过这个接收器在设备启动完成后重新注册所有必要的提醒。

## 3. 工作流程

### 3.1 设置/更新提醒

1.  **用户操作**: 用户在 `AddEditScheduleActivity` 界面创建新日程或编辑现有日程。
2.  **设置提醒时间**: 在该界面提供选项，让用户选择提醒时机（例如：不提醒、提前5分钟、提前15分钟、提前30分钟等）。这个设置值将被保存在日程数据中。
3.  **保存日程**: 用户点击保存按钮。
4.  **调度闹钟**:
    -   应用首先检查用户是否设置了提醒。
    -   如果设置了提醒，应用会根据日程的开始时间 (`startTime`) 和提前量（如15分钟）计算出精确的提醒触发时间。
    -   应用会创建一个指向 `ReminderBroadcastReceiver` 的 `PendingIntent`，并将该日程的唯一ID (`scheduleId`) 作为额外数据放入 `Intent` 中。
    -   使用 `AlarmManager` 的 `setExactAndAllowWhileIdle()` 方法设置一个一次性的、精确的闹钟，该闹钟将在计算出的提醒时间触发，并执行上述 `PendingIntent`。
    -   如果用户是**更新**一个已有提醒的日程，需要先根据 `scheduleId` **取消**旧的闹钟，然后再设置新的。

### 3.2 触发提醒

1.  **闹钟触发**: 在预设的提醒时间，`AlarmManager` 准时触发。
2.  **广播接收**: `ReminderBroadcastReceiver` 的 `onReceive()` 方法被调用，它会接收到包含 `scheduleId` 的 `Intent`。
3.  **启动服务**: `BroadcastReceiver` 的生命周期很短，不应执行耗时任务。因此，它的唯一任务就是立即启动 `ReminderService`，并将包含 `scheduleId` 的 `Intent` 传递给服务。

### 3.3 播报提醒

1.  **服务启动**: `ReminderService` 在后台启动。
2.  **获取数据**: 服务从接收到的 `Intent` 中提取出 `scheduleId`。
3.  **查询数据库**: 使用 `scheduleId` 查询 `Schedule.db` 数据库，获取该日程的完整信息（标题、地点、开始时间等）。
4.  **构建播报文本**: 根据查询到的信息，构建一个自然、友好的提醒语句，例如：“提醒您，您的日程 '团队会议' 将于15分钟后，在会议室A开始。”
5.  **初始化 TTS**: 获取 `TextToSpeech` 引擎的实例并进行初始化。
6.  **语音播报**: 在 TTS 引擎初始化成功后，调用其 `speak()` 方法，将构建好的提醒文本播报出来。
7.  **服务销毁**: 播报完成后，服务应自行停止 (`stopSelf()`)，以释放系统资源。

### 3.4 删除提醒

1.  **用户操作**: 用户在日程列表页（`ScheduleFragment`）长按并删除一个日程。
2.  **取消闹钟**: 在执行数据库删除操作的同时，应用必须根据该日程的 `scheduleId` 创建一个与设置时完全相同的 `PendingIntent`，然后调用 `AlarmManager` 的 `cancel()` 方法，将这个待办的闹钟从系统中取消，防止它在未来某个时间点错误地触发。

### 3.5 设备重启

1.  **系统启动**: 用户设备重新启动并完成初始化。
2.  **接收开机广播**: 系统会发出 `ACTION_BOOT_COMPLETED` 广播。我们预先注册的 `BootCompletedReceiver` 会接收到这个广播。
3.  **重新注册所有提醒**:
    -   `BootCompletedReceiver` 会立即查询数据库，找出所有**尚未完成**且设置了提醒的日程。
    -   遍历查询结果，为每一个符合条件的日程重新执行 **3.1 设置提醒** 中的第4步（调度闹钟），以确保所有提醒在重启后依然有效。

## 4. 数据库和模型变更

-   **`Schedule` 模型**: `Schedule.java` 模型中已存在 `reminderType` 字段，可直接用于存储用户的提醒偏好（例如，0=不提醒, 1=提前5分钟, 2=提前15分钟）。无需修改。
-   **数据库**: `ScheduleContract` 和 `ScheduleDbHelper` 同样已包含相应字段，无需修改。

## 5. AndroidManifest.xml 配置

需要在 `AndroidManifest.xml` 文件中进行以下配置：

1.  **声明 `Receiver`**:
    ```xml
    <receiver android:name=".reminders.ReminderBroadcastReceiver" android:enabled="true" />
    <receiver android:name=".reminders.BootCompletedReceiver" android:enabled="true">
        <intent-filter>
            <action android:name="android.intent.action.BOOT_COMPLETED" />
        </intent-filter>
    </receiver>
    ```
2.  **声明 `Service`**:
    ```xml
    <service android:name=".reminders.ReminderService" android:exported="false" />
    ```
3.  **申请权限**:
    ```xml
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <!-- 对于高版本Android，可能需要精确闹钟权限 -->
    <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
    ```

## 6. 实现步骤

1.  创建 `reminders` 包。
2.  在 `reminders` 包下创建 `ReminderBroadcastReceiver.java`。
3.  在 `reminders` 包下创建 `ReminderService.java`。
4.  在 `reminders` 包下创建 `BootCompletedReceiver.java`。
5.  按照 **第5节** 的内容更新 `AndroidManifest.xml`。
6.  修改 `AddEditScheduleActivity.java`，在 `saveSchedule()` 方法中增加调度和取消闹钟的逻辑。可以创建一个 `ReminderManager` 帮助类来封装 `AlarmManager` 的相关操作。
7.  修改 `ScheduleFragment.java`，在 `deleteSchedule()` 方法中增加取消闹钟的逻辑。

通过以上设计，我们可以构建一个功能完善、稳定可靠的日程语音提醒系统。 