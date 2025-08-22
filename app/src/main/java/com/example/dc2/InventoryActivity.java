package com.example.dc2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dc2.tabledao.BuyRecordDAO;
import com.example.dc2.tabledao.GameItemDAO;
import com.example.dc2.table.BuyRecord;
import com.example.dc2.table.GameItem;
import com.example.dc2.table.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryActivity extends AppCompatActivity
        implements InventoryAdapter.OnItemClickListener {

    private RecyclerView rvInventory;
    private InventoryAdapter inventoryAdapter;
    private List<BuyRecord> buyRecordList = new ArrayList<>();
    private Map<Integer, GameItem> itemMap = new HashMap<>();
    private ProgressBar progressBar;
    private TextView tvEmptyInventory;
    private BuyRecordDAO buyRecordDAO;
    private GameItemDAO gameItemDAO;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // 使用全局方法获取用户ID
        userId = BottomNavHelper.getUserId(this);

        if (userId == -1) {
            Toast.makeText(this, "用户未登录，请重新登录", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // 初始化视图
        rvInventory = findViewById(R.id.rvInventory);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyInventory = findViewById(R.id.tvEmptyInventory);
        // 设置底部导航栏（假设默认选中库存项）
        BottomNavHelper.setupBottomNavigation(this, R.id.nav_inventory);

        // 设置RecyclerView
        inventoryAdapter = new InventoryAdapter(this, buyRecordList, itemMap);
        rvInventory.setLayoutManager(new GridLayoutManager(this, 1));
        rvInventory.setAdapter(inventoryAdapter);

        // 设置点击监听器
        inventoryAdapter.setOnItemClickListener(this);

        // 初始化DAO
        buyRecordDAO = new BuyRecordDAO(this);
        gameItemDAO = new GameItemDAO(this);

        // 加载用户库存
        loadUserInventory();
    }

    /**
     * 加载用户库存
     */
    private void loadUserInventory() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyInventory.setVisibility(View.GONE);

        new Thread(() -> {
            // 获取用户购买记录
            buyRecordDAO.open();
            List<BuyRecord> records = buyRecordDAO.getBuyRecordsByUserId(userId, true);
            buyRecordDAO.close();

            // 获取饰品信息
            if (!records.isEmpty()) {
                gameItemDAO.open();
                for (BuyRecord record : records) {
                    GameItem item = gameItemDAO.getItemById(record.getItem_id());
                    if (item != null) {
                        itemMap.put(record.getItem_id(), item);
                    }
                }
                gameItemDAO.close();
            }

            // 更新UI
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                buyRecordList.clear();
                buyRecordList.addAll(records);
                inventoryAdapter.notifyDataSetChanged();

                if (records.isEmpty()) {
                    tvEmptyInventory.setVisibility(View.VISIBLE);
                }
            });
        }).start();
    }

    // 实现点击回调
    @Override
    public void onItemClick(BuyRecord record, GameItem item) {
        showSellOptionsDialog(record, item);
    }

    // 显示出售选项弹窗
    private void showSellOptionsDialog(BuyRecord record, GameItem item) {
        // 创建选项弹窗
        new AlertDialog.Builder(this)
                .setTitle("饰品操作: " + item.getItem_name())
                .setItems(new String[]{"上架出售"}, (dialog, which) -> {
                    if (which == 0) { // 上架出售选项
                        navigateToPublishPage(record, item);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 跳转到上架页面
    private void navigateToPublishPage(BuyRecord record, GameItem item) {
        Intent intent = new Intent(this, PublishSaleActivity.class);

        // 传递必要数据
        intent.putExtra("BUY_RECORD_ID", record.getRecord_id());
        intent.putExtra("ITEM_ID", item.getItem_id());
        intent.putExtra("ITEM_NAME", item.getItem_name());
        intent.putExtra("PURCHASE_PRICE", record.getPurchase_price());

        // 传递图片数据（可选）
        if (item.getItem_image() != null) {
            intent.putExtra("ITEM_IMAGE", item.getItem_image());
        }

        startActivity(intent);
    }
}