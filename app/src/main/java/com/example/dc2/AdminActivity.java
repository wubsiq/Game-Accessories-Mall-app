package com.example.dc2;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        DB dbHelper = new DB(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase(); // 关键！打开数据库连接

        // 获取用户ID
        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "用户信息获取失败", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 初始化按钮点击事件
        initButtonClickListeners();
    }

    /**
     * 初始化按钮点击事件
     */
    private void initButtonClickListeners() {
        // 用户管理按钮
        Button btnUserManagement = findViewById(R.id.btnUserManagement);
        btnUserManagement.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, UserManagementActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        // 饰品管理按钮
        Button btnItemManagement = findViewById(R.id.btnItemManagement);
        btnItemManagement.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, ItemManagementActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        // 出售记录按钮
        Button btnSaleRecords = findViewById(R.id.btnSaleRecords);
        btnSaleRecords.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, OnSaleItemsActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        // 求购记录按钮
        Button btnPurchaseRecords = findViewById(R.id.btnPurchaseRecords);
        btnPurchaseRecords.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, PurchaseRecordsActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });
    }
}