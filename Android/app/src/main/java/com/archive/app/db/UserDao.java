// UserDao.java
package com.archive.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.archive.app.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserDao {

    private ScheduleDbHelper dbHelper;

    public UserDao(Context context) {
        this.dbHelper = new ScheduleDbHelper(context);
    }

    /**
     * 增加一个用户 (Create)
     * @param user 要添加的用户对象（无需id）
     * @return 新用户的id，如果失败则返回-1
     */
    public long addUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_NAME_USERNAME, user.getUsername());
        values.put(UserContract.UserEntry.COLUMN_NAME_PASSWORD, user.getPassword());

        // 插入新行，返回新行的主键值
        long newRowId = db.insert(UserContract.UserEntry.TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }

    /**
     * 根据用户名查询用户 (Read)
     * @param username 要查询的用户名
     * @return 找到的User对象，如果未找到则返回 null
     */
    public User getUserByUsername(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                UserContract.UserEntry._ID,
                UserContract.UserEntry.COLUMN_NAME_USERNAME,
                UserContract.UserEntry.COLUMN_NAME_PASSWORD
        };

        String selection = UserContract.UserEntry.COLUMN_NAME_USERNAME + " = ?";
        String[] selectionArgs = { username };

        Cursor cursor = db.query(
                UserContract.UserEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        User foundUser = null;
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(UserContract.UserEntry._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_USERNAME));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_PASSWORD));
            foundUser = new User(id, name, password);
        }
        cursor.close();
        db.close();
        return foundUser;
    }

    /**
     * 更新用户信息（通常是更新密码） (Update)
     * @param user 包含新信息的用户对象 (必须有正确的id)
     * @return 受影响的行数
     */
    public int updateUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_NAME_PASSWORD, user.getPassword());

        String selection = UserContract.UserEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(user.getId()) };

        int count = db.update(
                UserContract.UserEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        db.close();
        return count;
    }

    /**
     * 根据用户名删除用户 (Delete)
     * @param username 要删除的用户名
     * @return 受影响的行数
     */
    public int deleteUserByUsername(String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = UserContract.UserEntry.COLUMN_NAME_USERNAME + " = ?";
        String[] selectionArgs = { username };

        int deletedRows = db.delete(UserContract.UserEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
        return deletedRows;
    }
}