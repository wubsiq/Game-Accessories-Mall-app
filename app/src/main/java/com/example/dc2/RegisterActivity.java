package com.example.dc2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dc2.table.User;
import com.example.dc2.tabledao.UserDAO;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etConfirmPassword, etPhone, etSecurityAnswer;
    private Spinner spSecurityQuestion;
    private CheckBox cbAgree;
    private Button btnRegister;

    private String selectedQuestion = ""; // 存储选择的密保问题

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 初始化视图
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhone = findViewById(R.id.etPhone);
        etSecurityAnswer = findViewById(R.id.etSecurityAnswer);
        spSecurityQuestion = findViewById(R.id.spSecurityQuestion);
        cbAgree = findViewById(R.id.cbAgree);
        btnRegister = findViewById(R.id.btnRegister);

        // 设置密保问题下拉列表
        setupSecurityQuestionSpinner();

        // 注册按钮点击事件
        btnRegister.setOnClickListener(v -> attemptRegister());
    }

    private void setupSecurityQuestionSpinner() {
        // 从资源文件中获取密保问题列表
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.security_questions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 设置适配器
        spSecurityQuestion.setAdapter(adapter);

        // 设置选择监听器
        spSecurityQuestion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedQuestion = parent.getItemAtPosition(position).toString();
                } else {
                    selectedQuestion = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedQuestion = "";
            }
        });
    }

    private void attemptRegister() {
        // 重置错误
        etUsername.setError(null);
        etPassword.setError(null);
        etConfirmPassword.setError(null);
        etPhone.setError(null);
        etSecurityAnswer.setError(null);

        // 获取输入值
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String securityAnswer = etSecurityAnswer.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // 验证用户名
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("请输入用户名");
            focusView = etUsername;
            cancel = true;
        } else if (username.length() < 4) {
            etUsername.setError("用户名至少4个字符");
            focusView = etUsername;
            cancel = true;
        }

        // 验证密码
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("请输入密码");
            focusView = etPassword;
            cancel = true;
        } else if (password.length() < 6) {
            etPassword.setError("密码长度至少6位");
            focusView = etPassword;
            cancel = true;
        }

        // 验证确认密码
        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("请确认密码");
            focusView = etConfirmPassword;
            cancel = true;
        } else if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("两次输入的密码不一致");
            focusView = etConfirmPassword;
            cancel = true;
        }

        // 验证手机号
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("请输入手机号");
            focusView = etPhone;
            cancel = true;
        } else if (phone.length() != 11) {
            etPhone.setError("手机号格式不正确");
            focusView = etPhone;
            cancel = true;
        } else {
            // 检查手机号是否已注册
            UserDAO userDAO = new UserDAO(this);
            userDAO.open();
            boolean isPhoneRegistered = userDAO.isPhoneRegistered(phone);
            userDAO.close();

            if (isPhoneRegistered) {
                showPhoneRegisteredDialog();
                focusView = etPhone;
                cancel = true;
            }
        }

        // 验证密保问题
        if (TextUtils.isEmpty(selectedQuestion)) {
            Toast.makeText(this, "请选择密保问题", Toast.LENGTH_SHORT).show();
            focusView = spSecurityQuestion;
            cancel = true;
        }

        // 验证密保答案
        if (TextUtils.isEmpty(securityAnswer)) {
            etSecurityAnswer.setError("请输入密保答案");
            focusView = etSecurityAnswer;
            cancel = true;
        }

        // 验证用户协议
        if (!cbAgree.isChecked()) {
            Toast.makeText(this, "请阅读并同意用户协议", Toast.LENGTH_SHORT).show();
            focusView = cbAgree;
            cancel = true;
        }

        if (cancel) {
            // 存在错误，聚焦到第一个错误字段
            focusView.requestFocus();
        } else {
            // 所有验证通过，执行注册
            registerUser(username, password, phone, selectedQuestion, securityAnswer);
        }
    }

    private void showPhoneRegisteredDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("手机号已注册")
                .setMessage("该手机号已被注册，您可以找回密码或使用其他手机号注册。")
                .setPositiveButton("找回密码", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到找回密码页面
                        Intent intent = new Intent(RegisterActivity.this, FindPasswordActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void registerUser(String username, String password, String phone,
                              String securityQuestion, String securityAnswer) {
        // 创建用户对象
        User newUser = new User(username, password, phone, securityQuestion, securityAnswer);

        // 保存到数据库
        UserDAO userDAO = new UserDAO(this);
        userDAO.open();
        long userId = userDAO.addUser(newUser);
        userDAO.close();

        if (userId != -1) {
            Toast.makeText(this, "注册成功！", Toast.LENGTH_SHORT).show();
            // 跳转到登录页面
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "注册失败，用户名可能已被使用", Toast.LENGTH_SHORT).show();
        }
    }
}