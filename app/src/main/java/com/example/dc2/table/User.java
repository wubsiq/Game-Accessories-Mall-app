package com.example.dc2.table;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class User {
    private int user_id;
    private String username;
    private String password;
    private double wallet_balance;
    private int is_admin;
    private byte[] avatar;
    private String phone;
    private String email;
    private String securityQuestion; // 新增密保问题
    private String securityAnswer;   // 新增密保答案

    public User() {
        // 默认构造器
    }

    // 完整构造器
    public User(int user_id, String username, String password, double wallet_balance,
                int is_admin, byte[] avatar, String phone, String email,
                String securityQuestion, String securityAnswer) {
        this.user_id = user_id;
        this.username = username;
        this.password = password;
        this.wallet_balance = wallet_balance;
        this.is_admin = is_admin;
        this.avatar = avatar;
        this.phone = phone;
        this.email = email;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
    }

    // 简化构造器（用于注册）
    public User(String username, String password, String phone,
                String securityQuestion, String securityAnswer) {
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.wallet_balance = 0.0; // 默认钱包余额为0
        this.is_admin = 0;         // 默认不是管理员
    }

    // Getters and Setters
    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getWallet_balance() {
        return wallet_balance;
    }

    public void setWallet_balance(double wallet_balance) {
        this.wallet_balance = wallet_balance;
    }

    public int getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(int is_admin) {
        this.is_admin = is_admin;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    // 辅助方法：将Bitmap转换为byte[]
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    // 辅助方法：将byte[]转换为Bitmap
    public static Bitmap byteArrayToBitmap(byte[] byteArray) {
        if (byteArray == null || byteArray.length == 0) return null;
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    public double getWallet_balance(double v) {return wallet_balance;
    }
}