package com.example.dc2;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dc2.tabledao.UserDAO;
import com.example.dc2.table.User;

public class FindPasswordActivity extends AppCompatActivity {

    private UserDAO userDAO;
    private User foundUser;

    // 视图组件
    private EditText etPhone;
    private Button btnFind;
    private TextView tvSecurityQuestion;
    private EditText etSecurityAnswer;
    private Button btnVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_find_password);

        // 初始化DAO
        userDAO = new UserDAO(this);

        // 初始化视图
        initViews();

        // 设置按钮点击事件
        setupButtons();
        // 设置返回按钮
        findViewById(R.id.ivBack).setOnClickListener(v -> onBackPressed());
    }

    private void initViews() {
        etPhone = findViewById(R.id.etPhone);
        btnFind = findViewById(R.id.btnFind1);
        tvSecurityQuestion = findViewById(R.id.tvSecurityQuestion);
        etSecurityAnswer = findViewById(R.id.etSecurityAnswer);
        btnVerify = findViewById(R.id.btnVerify);

        // 初始状态下密保区域不可见
        tvSecurityQuestion.setVisibility(View.GONE);
        etSecurityAnswer.setVisibility(View.GONE);
        btnVerify.setVisibility(View.GONE);
    }

    private void setupButtons() {
        // 查找按钮点击事件
        btnFind.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();

            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
                return;
            }

            // 在后台线程中查找用户
            findUserByPhone(phone);
        });

        // 验证按钮点击事件
        btnVerify.setOnClickListener(v -> {
            String answer = etSecurityAnswer.getText().toString().trim();

            if (TextUtils.isEmpty(answer)) {
                Toast.makeText(this, "请输入密保答案", Toast.LENGTH_SHORT).show();
                return;
            }

            // 验证密保答案
            verifySecurityAnswer(answer);
        });
    }

    private void findUserByPhone(String phone) {
        new Thread(() -> {
            userDAO.open();
            foundUser = userDAO.getUserByPhone(phone);
            userDAO.close();

            runOnUiThread(() -> {
                if (foundUser != null) {
                    // 显示密保问题
                    tvSecurityQuestion.setText("密保问题: " + foundUser.getSecurityQuestion());
                    tvSecurityQuestion.setVisibility(View.VISIBLE);
                    etSecurityAnswer.setVisibility(View.VISIBLE);
                    btnVerify.setVisibility(View.VISIBLE);

                    Toast.makeText(this, "找到用户，请回答密保问题", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "未找到该手机号对应的用户", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void verifySecurityAnswer(String answer) {
        new Thread(() -> {
            userDAO.open();
            boolean isVerified = userDAO.verifySecurityAnswer(foundUser.getUsername(), answer);
            userDAO.close();

            runOnUiThread(() -> {
                if (isVerified) {
                    // 验证成功，显示重置密码对话框
                    showResetPasswordDialog();
                } else {
                    Toast.makeText(this, "密保答案错误", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void showResetPasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_reset_password, null);
        final EditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);
        final EditText etConfirmPassword = dialogView.findViewById(R.id.etConfirmPassword);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("重置密码")
                .setView(dialogView)
                .setPositiveButton("保存", (dialog, which) -> {
                    String newPassword = etNewPassword.getText().toString().trim();
                    String confirmPassword = etConfirmPassword.getText().toString().trim();

                    if (TextUtils.isEmpty(newPassword)) {
                        Toast.makeText(this, "请输入新密码", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!newPassword.equals(confirmPassword)) {
                        Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 重置密码
                    resetPassword(newPassword);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void resetPassword(String newPassword) {
        new Thread(() -> {
            userDAO.open();
            int result = userDAO.updatePassword(foundUser.getUsername(), newPassword);
            userDAO.close();

            runOnUiThread(() -> {
                if (result > 0) {
                    Toast.makeText(this, "密码重置成功，请使用新密码登录", Toast.LENGTH_SHORT).show();
                    finish(); // 关闭当前页面
                } else {
                    Toast.makeText(this, "密码重置失败", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}