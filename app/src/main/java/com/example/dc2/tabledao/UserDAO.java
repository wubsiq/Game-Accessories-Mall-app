package com.example.dc2.tabledao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.dc2.DB;
import com.example.dc2.table.User;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private DB dbHelper;
    private SQLiteDatabase db;

    // 表字段常量（与DB.java中的User表保持一致）
    public static final String TABLE_USERS = "User";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_WALLET_BALANCE = "wallet_balance";
    public static final String KEY_IS_ADMIN = "is_admin";
    public static final String KEY_AVATAR = "avatar";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_SECURITY_QUESTION = "security_question";
    public static final String KEY_SECURITY_ANSWER = "security_answer";

    public UserDAO(Context context) {
        dbHelper = new DB(context);
    }

    // 打开数据库连接
    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    // 关闭数据库连接
    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    // 添加用户（包含密保字段）
    public long addUser(User user) {
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_WALLET_BALANCE, user.getWallet_balance());
        values.put(KEY_IS_ADMIN, user.getIs_admin());
        values.put(KEY_AVATAR, user.getAvatar());
        values.put(KEY_PHONE, user.getPhone());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_SECURITY_QUESTION, user.getSecurityQuestion());
        values.put(KEY_SECURITY_ANSWER, user.getSecurityAnswer());

        return db.insert(TABLE_USERS, null, values);
    }

    // 根据ID获取用户（包含密保字段）
    @SuppressLint("Range")
    public User getUserById(int userId) {
        Cursor cursor = db.query(TABLE_USERS, null, KEY_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User();
            user.setUser_id(cursor.getInt(cursor.getColumnIndex(KEY_USER_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndex(KEY_USERNAME)));
            user.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
            user.setWallet_balance(cursor.getDouble(cursor.getColumnIndex(KEY_WALLET_BALANCE)));
            user.setIs_admin(cursor.getInt(cursor.getColumnIndex(KEY_IS_ADMIN)));
            user.setAvatar(cursor.getBlob(cursor.getColumnIndex(KEY_AVATAR)));
            user.setPhone(cursor.getString(cursor.getColumnIndex(KEY_PHONE)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
            user.setSecurityQuestion(cursor.getString(cursor.getColumnIndex(KEY_SECURITY_QUESTION)));
            user.setSecurityAnswer(cursor.getString(cursor.getColumnIndex(KEY_SECURITY_ANSWER)));

            cursor.close();
            return user;
        }
        return null;
    }

    // 根据用户名获取用户（包含密保字段）
    @SuppressLint("Range")
    public User getUserByUsername(String username) {
        Cursor cursor = db.query(TABLE_USERS, null, KEY_USERNAME + "=?",
                new String[]{username}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User();
            user.setUser_id(cursor.getInt(cursor.getColumnIndex(KEY_USER_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndex(KEY_USERNAME)));
            user.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
            user.setWallet_balance(cursor.getDouble(cursor.getColumnIndex(KEY_WALLET_BALANCE)));
            user.setIs_admin(cursor.getInt(cursor.getColumnIndex(KEY_IS_ADMIN)));
            user.setAvatar(cursor.getBlob(cursor.getColumnIndex(KEY_AVATAR)));
            user.setPhone(cursor.getString(cursor.getColumnIndex(KEY_PHONE)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
            user.setSecurityQuestion(cursor.getString(cursor.getColumnIndex(KEY_SECURITY_QUESTION)));
            user.setSecurityAnswer(cursor.getString(cursor.getColumnIndex(KEY_SECURITY_ANSWER)));

            cursor.close();
            return user;
        }
        return null;
    }
    // 根据手机号获取用户（包含密保字段）
    @SuppressLint("Range")
    public User getUserByPhone(String phone) {
        Cursor cursor = db.query(TABLE_USERS, null, KEY_PHONE + "=?",
                new String[]{phone}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User();
            user.setUser_id(cursor.getInt(cursor.getColumnIndex(KEY_USER_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndex(KEY_USERNAME)));
            user.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
            user.setWallet_balance(cursor.getDouble(cursor.getColumnIndex(KEY_WALLET_BALANCE)));
            user.setIs_admin(cursor.getInt(cursor.getColumnIndex(KEY_IS_ADMIN)));
            user.setAvatar(cursor.getBlob(cursor.getColumnIndex(KEY_AVATAR)));
            user.setPhone(cursor.getString(cursor.getColumnIndex(KEY_PHONE)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
            user.setSecurityQuestion(cursor.getString(cursor.getColumnIndex(KEY_SECURITY_QUESTION)));
            user.setSecurityAnswer(cursor.getString(cursor.getColumnIndex(KEY_SECURITY_ANSWER)));

            cursor.close();
            return user;
        }
        return null;
    }
    // 更新用户信息（包含密保字段）
    public int updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_WALLET_BALANCE, user.getWallet_balance());
        values.put(KEY_IS_ADMIN, user.getIs_admin());
        values.put(KEY_AVATAR, user.getAvatar());
        values.put(KEY_PHONE, user.getPhone());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_SECURITY_QUESTION, user.getSecurityQuestion());
        values.put(KEY_SECURITY_ANSWER, user.getSecurityAnswer());

        return db.update(TABLE_USERS, values, KEY_USER_ID + "=?",
                new String[]{String.valueOf(user.getUser_id())});
    }

    // 删除用户
    public int deleteUser(int userId) {
        return db.delete(TABLE_USERS, KEY_USER_ID + "=?",
                new String[]{String.valueOf(userId)});
    }

    // 获取所有用户（包含密保字段）
    @SuppressLint("Range")
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        Cursor cursor = db.query(TABLE_USERS, null, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                User user = new User();
                user.setUser_id(cursor.getInt(cursor.getColumnIndex(KEY_USER_ID)));
                user.setUsername(cursor.getString(cursor.getColumnIndex(KEY_USERNAME)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
                user.setWallet_balance(cursor.getDouble(cursor.getColumnIndex(KEY_WALLET_BALANCE)));
                user.setIs_admin(cursor.getInt(cursor.getColumnIndex(KEY_IS_ADMIN)));
                user.setAvatar(cursor.getBlob(cursor.getColumnIndex(KEY_AVATAR)));
                user.setPhone(cursor.getString(cursor.getColumnIndex(KEY_PHONE)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
                user.setSecurityQuestion(cursor.getString(cursor.getColumnIndex(KEY_SECURITY_QUESTION)));
                user.setSecurityAnswer(cursor.getString(cursor.getColumnIndex(KEY_SECURITY_ANSWER)));

                userList.add(user);
            }
            cursor.close();
        }
        return userList;
    }

    // 根据用户名和密保答案验证用户（用于找回密码）
    public boolean verifySecurityAnswer(String username, String securityAnswer) {
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{KEY_SECURITY_ANSWER},
                KEY_USERNAME + " = ?",
                new String[]{username},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String storedAnswer = cursor.getString(cursor.getColumnIndex(KEY_SECURITY_ANSWER));
            cursor.close();
            return storedAnswer != null && storedAnswer.equals(securityAnswer);
        }
        return false;
    }

    // 更新用户密码（用于找回密码）
    public int updatePassword(String username, String newPassword) {
        ContentValues values = new ContentValues();
        values.put(KEY_PASSWORD, newPassword);

        return db.update(TABLE_USERS, values, KEY_USERNAME + " = ?", new String[]{username});
    }

    // 检查手机号是否已注册
    public boolean isPhoneRegistered(String phone) {
        SQLiteDatabase dbRead = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_USERS + " WHERE " + KEY_PHONE + " = ?";
        Cursor cursor = dbRead.rawQuery(selectQuery, new String[]{phone});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }
}