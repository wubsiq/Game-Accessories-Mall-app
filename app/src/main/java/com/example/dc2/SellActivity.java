package com.example.dc2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dc2.tabledao.ItemForSaleDAO;
import com.example.dc2.table.ItemForSale;
import java.util.ArrayList;
import java.util.List;
import com.example.dc2.BottomNavHelper;

public class SellActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SellListAdapter adapter;
    private int userId;
    private List<ItemForSale> sellList = new ArrayList<>();
    private ItemForSaleDAO itemForSaleDAO;
    private int currentUserId = 1; // 示例用户ID，实际应从会话或登录状态获取

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);
// 使用全局方法获取用户ID
        userId = BottomNavHelper.getUserId(this);

        if (userId == -1) {
            Toast.makeText(this, "用户未登录，请重新登录", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        // 设置底部导航栏
        BottomNavHelper.setupBottomNavigation(this, R.id.nav_sell);

        // 初始化DAO
        itemForSaleDAO = new ItemForSaleDAO(this);

        // 设置RecyclerView
        recyclerView = findViewById(R.id.recyclerViewSell);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SellListAdapter(this, sellList);
        recyclerView.setAdapter(adapter);

        // 加载用户出售记录
        loadUserSellItems();
    }

    private void loadUserSellItems() {
        new Thread(() -> {
            itemForSaleDAO.open();
            // 根据user_id获取出售记录
            List<ItemForSale> items = itemForSaleDAO.getItemsForSaleByItemId(
                    currentUserId, "user_id=?", new String[]{String.valueOf(currentUserId)});
            itemForSaleDAO.close();

            runOnUiThread(() -> {
                sellList.clear();
                sellList.addAll(items);
                adapter.notifyDataSetChanged();

                if (items.isEmpty()) {
                    Toast.makeText(this, "暂无出售记录", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}