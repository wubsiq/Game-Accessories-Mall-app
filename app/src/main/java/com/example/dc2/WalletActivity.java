package com.example.dc2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dc2.tabledao.UserDAO;
import com.example.dc2.table.User;

public class WalletActivity extends AppCompatActivity {

    private UserDAO userDAO;
    private int userId;
    private User currentUser;

    // 视图组件
    private View mainView;
    private TextView tvBalance;
    private RadioGroup rgAmount;
    private RadioButton rb5, rb10, rb50, rb100, rbCustom;
    private TextView tvCustomAmount;
    private Button btnRecharge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wallet);

        // 设置返回按钮
        findViewById(R.id.ivBack).setOnClickListener(v -> onBackPressed());
        // 初始化DAO
        userDAO = new UserDAO(this);

        // 获取用户ID
        userId = BottomNavHelper.getUserId(this);
        if (userId == -1) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // 初始化视图
        initViews();

        // 加载用户钱包余额
        loadWalletBalance();

        // 设置充值按钮点击事件
        setupRechargeButton();
    }

    private void initViews() {
        mainView = findViewById(R.id.main);
        tvBalance = findViewById(R.id.tvBalance1);
        rgAmount = findViewById(R.id.rgAmount);
        rb5 = findViewById(R.id.rb5);
        rb10 = findViewById(R.id.rb10);
        rb50 = findViewById(R.id.rb50);
        rb100 = findViewById(R.id.rb100);
        rbCustom = findViewById(R.id.rbCustom);
        tvCustomAmount = findViewById(R.id.tvCustomAmount);
        btnRecharge = findViewById(R.id.btnRecharge);

        // 设置系统栏Insets
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, 0, 0, 0);
            return insets;
        });

        // 自定义金额输入框初始隐藏
        tvCustomAmount.setVisibility(View.GONE);

        // 自定义金额单选框选中事件
        rbCustom.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tvCustomAmount.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });
    }

    private void loadWalletBalance() {
        new Thread(() -> {
            userDAO.open();
            currentUser = userDAO.getUserById(userId);
            userDAO.close();

            runOnUiThread(() -> {
                if (currentUser != null) {
                    tvBalance.setText("¥" + String.format("%.2f", currentUser.getWallet_balance()));
                } else {
                    Toast.makeText(WalletActivity.this, "获取钱包信息失败", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void setupRechargeButton() {
        btnRecharge.setOnClickListener(v -> {
            // 获取选中的充值金额
            double rechargeAmount = 0;
            int checkedId = rgAmount.getCheckedRadioButtonId();

            if (checkedId == R.id.rb5) {
                rechargeAmount = 5.0;
            } else if (checkedId == R.id.rb10) {
                rechargeAmount = 10.0;
            } else if (checkedId == R.id.rb50) {
                rechargeAmount = 50.0;
            } else if (checkedId == R.id.rb100) {
                rechargeAmount = 100.0;
            } else if (checkedId == R.id.rbCustom) {
                String customAmount = tvCustomAmount.getText().toString().trim();
                if (customAmount.isEmpty()) {
                    Toast.makeText(this, "请输入自定义金额", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    rechargeAmount = Double.parseDouble(customAmount);
                    if (rechargeAmount <= 0) {
                        Toast.makeText(this, "金额必须大于0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "请输入正确的金额", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(this, "请选择充值金额", Toast.LENGTH_SHORT).show();
                return;
            }

            // 执行充值
            rechargeWallet(rechargeAmount);
        });
    }

    private void rechargeWallet(double amount) {
        new Thread(() -> {
            try {
                userDAO.open();
                currentUser = userDAO.getUserById(userId);

                if (currentUser != null) {
                    // 更新钱包余额
                    double newBalance = currentUser.getWallet_balance() + amount;
                    currentUser.setWallet_balance(newBalance);

                    // 更新数据库
                    userDAO.updateUser(currentUser);

                    runOnUiThread(() -> {
                        tvBalance.setText("¥" + String.format("%.2f", newBalance));
                        Toast.makeText(WalletActivity.this, "充值成功，充值金额: ¥" + amount, Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(WalletActivity.this, "获取用户信息失败", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(WalletActivity.this, "充值失败", Toast.LENGTH_SHORT).show());
            } finally {
                userDAO.close();
            }
        }).start();
    }
}