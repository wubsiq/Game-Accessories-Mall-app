package com.example.dc2;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dc2.tabledao.BuyRecordDAO;
import com.example.dc2.tabledao.GameItemDAO;
import com.example.dc2.tabledao.UserDAO;
import com.example.dc2.table.BuyRecord;
import com.example.dc2.table.GameItem;
import com.example.dc2.table.User;

import java.util.ArrayList;
import java.util.List;

public class PurchaseRecordsActivity extends AppCompatActivity {

    private BuyRecordDAO buyRecordDAO;
    private UserDAO userDAO;
    private GameItemDAO gameItemDAO;
    private List<BuyRecord> purchaseRecordList = new ArrayList<>();
    private PurchaseRecordAdapter adapter;

    // 视图组件
    private ImageView ivBack;
    private TextView tvTitle;
    private RecyclerView rvPurchaseRecords;
    private TextView tvEmptyRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_purchase_records);

        // 初始化DAO
        buyRecordDAO = new BuyRecordDAO(this);
        userDAO = new UserDAO(this);
        gameItemDAO = new GameItemDAO(this);

        // 初始化视图
        initViews();

        // 加载购买记录数据
        loadPurchaseRecords();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        tvTitle = findViewById(R.id.tvTitle);
        rvPurchaseRecords = findViewById(R.id.rvPurchaseRecords);
        tvEmptyRecords = findViewById(R.id.tvEmptyRecords);

        // 设置返回按钮
        ivBack.setOnClickListener(v -> onBackPressed());

        // 设置标题
        tvTitle.setText("购买记录");

        // 设置RecyclerView
        rvPurchaseRecords.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PurchaseRecordAdapter(this, purchaseRecordList);
        rvPurchaseRecords.setAdapter(adapter);
    }

    private void loadPurchaseRecords() {
        new Thread(() -> {
            buyRecordDAO.open();
            List<BuyRecord> records = buyRecordDAO.getAllBuyRecords(true);
            buyRecordDAO.close();

            // 关联用户信息
            for (BuyRecord record : records) {
                userDAO.open();
                User user = userDAO.getUserById(record.getUser_id());
                userDAO.close();
                record.setUser(user);
            }

            runOnUiThread(() -> {
                purchaseRecordList.clear();
                purchaseRecordList.addAll(records);
                adapter.notifyDataSetChanged();

                // 显示/隐藏空记录提示
                if (purchaseRecordList.isEmpty()) {
                    rvPurchaseRecords.setVisibility(View.GONE);
                    tvEmptyRecords.setVisibility(View.VISIBLE);
                } else {
                    rvPurchaseRecords.setVisibility(View.VISIBLE);
                    tvEmptyRecords.setVisibility(View.GONE);
                }
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (buyRecordDAO != null) buyRecordDAO.close();
        if (userDAO != null) userDAO.close();
        if (gameItemDAO != null) gameItemDAO.close();
    }
}