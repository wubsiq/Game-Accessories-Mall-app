package com.example.dc2;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dc2.table.User;

import java.util.function.Function;

public class EditUserDialog extends Dialog {

    private Context context;
    private User user;
    private Function<User, Boolean> callback;
    private EditText etUsername, etPassword, etPhone, etEmail;
    private EditText etSecurityQuestion, etSecurityAnswer;
    private EditText etWalletBalance, etIsAdmin;

    public EditUserDialog(Context context, User user, Function<User, Boolean> callback) {
        super(context);
        this.context = context;
        this.user = user;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_user);

        // 初始化视图
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etSecurityQuestion = findViewById(R.id.etSecurityQuestion);
        etSecurityAnswer = findViewById(R.id.etSecurityAnswer);
        etWalletBalance = findViewById(R.id.etWalletBalance);
        etIsAdmin = findViewById(R.id.etIsAdmin);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);

        // 填充现有用户数据
        etUsername.setText(user.getUsername());
        etPhone.setText(user.getPhone());
        etEmail.setText(user.getEmail());
        etSecurityQuestion.setText(user.getSecurityQuestion());
        etSecurityAnswer.setText(user.getSecurityAnswer());
        etWalletBalance.setText(String.valueOf(user.getWallet_balance()));
        etIsAdmin.setText(String.valueOf(user.getIs_admin()));

        // 保存按钮点击事件
        btnSave.setOnClickListener(v -> saveUserChanges());

        // 取消按钮点击事件
        btnCancel.setOnClickListener(v -> dismiss());
    }

    /**
     * 保存用户修改
     */
    private void saveUserChanges() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String securityQuestion = etSecurityQuestion.getText().toString().trim();
        String securityAnswer = etSecurityAnswer.getText().toString().trim();

        try {
            double walletBalance = Double.parseDouble(etWalletBalance.getText().toString().trim());
            int isAdmin = Integer.parseInt(etIsAdmin.getText().toString().trim());

            // 创建更新后的用户对象
            User updatedUser = new User(
                    user.getUser_id(),
                    username,
                    password.isEmpty() ? user.getPassword() : password,
                    walletBalance,
                    isAdmin,
                    user.getAvatar(),
                    phone,
                    email,
                    securityQuestion,
                    securityAnswer
            );

            // 调用回调函数
            if (callback.apply(updatedUser)) {
                dismiss();
            } else {
                Toast.makeText(context, "更新失败", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(context, "输入格式错误", Toast.LENGTH_SHORT).show();
        }
    }
}
