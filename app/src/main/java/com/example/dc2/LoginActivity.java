package com.example.dc2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dc2.tabledao.UserDAO;
import com.example.dc2.table.User;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private CheckBox cbRemember;
    private Button btnLogin, btnRegister;
    private ProgressBar progressBar; // 加载进度条
    private TextView tvCopyright,tvForgotPassword; // 版权信息

    // SharedPreferences相关常量
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_REMEMBER = "remember";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化视图
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        cbRemember = findViewById(R.id.cbRemember);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar); // 初始化进度条
        tvCopyright = findViewById(R.id.tvCopyright); // 版权信息
        tvForgotPassword=findViewById(R.id.tvForgotPassword);

        // 初始化数据库操作对象
        userDAO = new UserDAO(this);

        // 初始化SharedPreferences
        sp = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();

        // 加载记住的凭证
        loadRememberedCredentials();

        // 设置登录按钮点击事件
        btnLogin.setOnClickListener(v -> attemptLogin());

        // 设置注册按钮点击事件
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // 设置版权信息点击事件 - 访问管理员页面
        tvCopyright.setOnClickListener(v -> goToAdminActivity());

        tvForgotPassword.setOnClickListener(v -> goToFindPassword());
    }



    /**
     * 加载记住的用户名和密码
     */
    private void loadRememberedCredentials() {
        boolean remember = sp.getBoolean(KEY_REMEMBER, false);
        if (remember) {
            String username = sp.getString(KEY_USERNAME, "");
            String password = sp.getString(KEY_PASSWORD, "");

            etUsername.setText(username);
            etPassword.setText(password);
            cbRemember.setChecked(true);
        }
    }

    /**
     * 保存记住的用户名和密码
     */
    private void saveRememberedCredentials(String username, String password) {
        editor.putBoolean(KEY_REMEMBER, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    /**
     * 清除记住的登录信息
     */
    private void clearRememberedCredentials() {
        editor.clear();
        editor.apply();
    }

    /**
     * 尝试登录
     */
    private void attemptLogin() {
        // 重置错误
        etUsername.setError(null);
        etPassword.setError(null);

        // 获取输入值
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        boolean cancel = false;

        // 检查手机号是否有效
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("请输入手机号");
            cancel = true;
        } else if (username.length() != 11) {
            etUsername.setError("手机号格式不正确");
            cancel = true;
        }

        // 检查密码是否有效
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("请输入密码");
            cancel = true;
        } else if (password.length() < 6) {
            etPassword.setError("密码长度至少6位");
            cancel = true;
        }

        if (cancel) {
            return;
        }

        // 显示加载进度
        progressBar.setVisibility(ProgressBar.VISIBLE);
        btnLogin.setEnabled(false);

        // 在子线程中执行数据库查询
        new Thread(() -> {
            userDAO.open(); // 打开数据库连接
            User user = userDAO.getUserByPhone(username);
            userDAO.close(); // 关闭数据库连接

            final boolean loginSuccess;
            final int userId;
            final String errorMsg;

            if (user == null) {
                loginSuccess = false;
                userId = -1;
                errorMsg = "用户不存在";
            } else if (!user.getPassword().equals(password)) {
                loginSuccess = false;
                userId = -1;
                errorMsg = "密码错误";
            } else {
                loginSuccess = true;
                userId = user.getUser_id();
                errorMsg = "";
            }

            // 在主线程中处理结果
            new Handler(Looper.getMainLooper()).post(() -> {
                progressBar.setVisibility(ProgressBar.GONE);
                btnLogin.setEnabled(true);

                if (loginSuccess) {
                    // 登录成功，保存用户信息
                    if (cbRemember.isChecked()) {
                        saveRememberedCredentials(username, password);
                    } else {
                        clearRememberedCredentials();
                    }

                    // 保存用户ID到全局
                    BottomNavHelper.saveUserId(LoginActivity.this, userId);

                    // 跳转到主页面
                    BottomNavHelper.navigateToActivity(
                            LoginActivity.this,
                            MainActivity.class,
                            userId
                    );
                    finish();
                } else {
                    // 登录失败，显示错误信息
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    /**
     * 跳转到管理员页面
     */
    private void goToAdminActivity() {
        // 使用全局方法获取用户ID
        int userId = BottomNavHelper.getUserId(this);

        if (userId == -1) {
            Toast.makeText(this, "请先登录账号", Toast.LENGTH_SHORT).show();
            return;
        }


        // 在后台线程中检查管理员权限
        new Thread(() -> {
            userDAO.open();
            User user = userDAO.getUserById(userId);
            userDAO.close();

            runOnUiThread(() -> {
                if (user != null && user.getIs_admin() == 1) {
                    BottomNavHelper.navigateToActivity(
                            LoginActivity.this,
                            AdminActivity.class,
                            userId
                    );
                } else {
                    Toast.makeText(LoginActivity.this, "您不是管理员，无权限访问", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
    private void goToFindPassword(){
        Intent intent = new Intent(LoginActivity.this, FindPasswordActivity.class);
        startActivity(intent);

    }

}