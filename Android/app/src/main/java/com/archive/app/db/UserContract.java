// UserContract.java
package com.archive.app.db;

import android.provider.BaseColumns;

public final class UserContract {
    // 为防止意外实例化，构造函数设为 private
    private UserContract() {}

    /* 内部类，定义 user 表的内容 */
    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_PASSWORD = "password";
    }
}