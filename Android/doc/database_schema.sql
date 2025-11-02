-- SQL schema based on the Android SQLite database structure
-- for use with MySQL.

-- --- schedules table ---
-- Derived from ScheduleContract.java and ScheduleDbHelper.java
CREATE TABLE `schedules` (
    `_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `server_id` INT,
    `title` VARCHAR(255),
    `description` TEXT,
    `location` VARCHAR(255),
    `start_time` BIGINT,
    `end_time` BIGINT,
    `reminder_type` INT,
    `voice_theme` VARCHAR(255),
    `is_completed` TINYINT(1) DEFAULT 0,
    `is_synced` TINYINT(1) DEFAULT 0,
    `repeat_mode` INT DEFAULT 0
);

-- --- user table ---
-- Derived from UserContract.java and ScheduleDbHelper.java
CREATE TABLE `user` (
    `_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(255) UNIQUE,
    `password` VARCHAR(255)
); 