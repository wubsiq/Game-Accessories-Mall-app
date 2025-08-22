package com.example.dc2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.dc2.tabledao.UserDAO;
import com.example.dc2.table.User;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvWelcome, tvUsername, tvWalletBalance, tvPhone, tvEmail;
    private ImageView ivAvatar;
    private Button btnLogout, btnViewOrders, btnViewWallet;
    private UserDAO userDAO;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // 设置底部导航栏
        BottomNavHelper.setupBottomNavigation(this, R.id.nav_profile);

        // 初始化视图
        tvWelcome = findViewById(R.id.tvWelcome);
        tvUsername = findViewById(R.id.tvUsername);
        tvWalletBalance = findViewById(R.id.tvWalletBalance);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);
        ivAvatar = findViewById(R.id.ivAvatar);
        btnLogout = findViewById(R.id.btnLogout);
        btnViewOrders = findViewById(R.id.btnViewOrders);
        btnViewWallet = findViewById(R.id.btnViewWallet);

        // 初始化DAO
        userDAO = new UserDAO(this);

        // 获取用户ID
        userId = BottomNavHelper.getUserId(this);
        if (userId == -1) {
            userId = getIntent().getIntExtra("user_id", -1);
        }

        if (userId == -1) {
            Toast.makeText(this, "用户ID错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 加载用户信息
        loadUserInfo();

        // 退出登录按钮
        btnLogout.setOnClickListener(v -> logout());

        // 查看订单按钮
        btnViewOrders.setOnClickListener(v -> viewOrders());

        // 查看钱包按钮
        btnViewWallet.setOnClickListener(v -> viewWallet());

        // 设置窗口内边距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * 加载用户信息
     */
    private void loadUserInfo() {
        new Handler(Looper.getMainLooper()).post(() -> {
            tvWelcome.setText("欢迎回来，");
            tvWalletBalance.setText("加载中...");
        });

        new Thread(() -> {
            userDAO.open();
            User user = userDAO.getUserById(userId);
            userDAO.close();

            if (user != null) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    tvWelcome.append(user.getUsername() + " 登录");
                    tvUsername.setText("用户名: " + user.getUsername());
                    tvWalletBalance.setText("钱包余额: ¥" + String.format("%.2f", user.getWallet_balance()));
                    tvPhone.setText("手机号: " + user.getPhone());
                    tvEmail.setText("邮箱: " + (user.getEmail() != null ? user.getEmail() : "未设置"));

                    // 加载头像
                    if (user.getAvatar() != null) {
                        Glide.with(this)
                                .load(User.byteArrayToBitmap(user.getAvatar()))
                                .circleCrop()
                                .into(ivAvatar);
                    } else {
                        ivAvatar.setImageResource(R.drawable.default_avatar);
                    }
                });
            } else {
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(ProfileActivity.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
                    tvWalletBalance.setText("钱包余额: ¥0.00");
                });
            }
        }).start();
    }

    /**
     * 退出登录
     */
    private void logout() {
        BottomNavHelper.clearUserId(this);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * 查看订单
     */
    private void viewOrders() {
        Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra("user_id", userId);
        startActivity(intent);
    }

    /**
     * 查看钱包（可跳转到钱包详情页）
     */
    private void viewWallet() {
        Intent intent = new Intent(this, WalletActivity.class);
        intent.putExtra("user_id", userId);
        startActivity(intent);
    }
}