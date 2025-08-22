package com.example.dc2;

import android.content.Intent;
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
import com.example.dc2.table.BuyRecord;
import com.example.dc2.table.GameItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {

    private BuyRecordDAO buyRecordDAO;
    private GameItemDAO gameItemDAO;
    private int userId;
    private List<BuyRecord> orderList = new ArrayList<>();
    private OrderListAdapter adapter;

    // 视图组件
    private ImageView ivBack;
    private TextView tvTitle;
    private RecyclerView rvOrderList;
    private TextView tvEmptyOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order);

        // 初始化DAO
        buyRecordDAO = new BuyRecordDAO(this);
        gameItemDAO = new GameItemDAO(this);

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

        // 加载订单数据
        loadOrderData();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        tvTitle = findViewById(R.id.tvTitle);
        rvOrderList = findViewById(R.id.rvOrderList);
        tvEmptyOrder = findViewById(R.id.tvEmptyOrder);

        // 设置返回按钮
        ivBack.setOnClickListener(v -> onBackPressed());

        // 设置标题
        tvTitle.setText("购买记录");

        // 设置RecyclerView
        rvOrderList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderListAdapter(this, orderList);
        rvOrderList.setAdapter(adapter);
    }

    private void loadOrderData() {
        new Thread(() -> {
            buyRecordDAO.open();
            List<BuyRecord> records = buyRecordDAO.getBuyRecordsByUserId(userId,true);
            buyRecordDAO.close();

            // 收集所有需要查询的商品ID
            List<Integer> itemIds = new ArrayList<>();
            for (BuyRecord record : records) {
                itemIds.add(record.getItem_id());
            }

            // 一次性获取所有商品信息
            gameItemDAO.open();
            Map<Integer, GameItem> gameItemMap = new HashMap<>();
            for (int itemId : itemIds) {
                GameItem item = gameItemDAO.getItemWithPriceInfo(itemId);
                if (item != null) {
                    gameItemMap.put(itemId, item);
                }
            }
            gameItemDAO.close();

            // 关联商品信息到订单记录
            for (BuyRecord record : records) {
                GameItem item = gameItemMap.get(record.getItem_id());
                if (item != null) {
                    record.setGameItem(item);
                }
            }

            runOnUiThread(() -> {
                orderList.clear();
                orderList.addAll(records);
                adapter.notifyDataSetChanged();

                // 显示/隐藏空订单提示
                if (orderList.isEmpty()) {
                    rvOrderList.setVisibility(View.GONE);
                    tvEmptyOrder.setVisibility(View.VISIBLE);
                } else {
                    rvOrderList.setVisibility(View.VISIBLE);
                    tvEmptyOrder.setVisibility(View.GONE);
                }
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (buyRecordDAO != null) buyRecordDAO.close();
        if (gameItemDAO != null) gameItemDAO.close();
    }
}