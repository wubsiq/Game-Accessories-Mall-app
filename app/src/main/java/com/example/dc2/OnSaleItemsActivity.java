package com.example.dc2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dc2.table.GameItem;
import com.example.dc2.table.ItemForSale;
import com.example.dc2.table.User;
import com.example.dc2.tabledao.GameItemDAO;
import com.example.dc2.tabledao.ItemForSaleDAO;
import com.example.dc2.tabledao.UserDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class OnSaleItemsActivity extends AppCompatActivity
        implements OnSaleItemAdapter.OnRemoveClickListener {

    private static final String TAG = "MySaleItemsActivity";

    private Executor executor = Executors.newSingleThreadExecutor();

    // DAOs
    private ItemForSaleDAO itemForSaleDAO;
    private UserDAO userDAO;
    private GameItemDAO gameItemDAO;

    // Views
    private RecyclerView rvSaleItems;
    private TextView tvEmpty;

    // Adapter
    private OnSaleItemAdapter adapter;
    private List<ItemForSale> saleItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_sale_items);

        // 初始化DAO
        itemForSaleDAO = new ItemForSaleDAO(this);
        userDAO = new UserDAO(this);
        gameItemDAO = new GameItemDAO(this);

        // 初始化视图
        initViews();

        // 加载在售饰品
        loadMySaleItems();
    }

    private void initViews() {
        // 返回按钮
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        // 标题
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("在售饰品");

        // 列表和空状态
        rvSaleItems = findViewById(R.id.rvMySaleItems);
        tvEmpty = findViewById(R.id.tvEmpty);

        // 设置RecyclerView
        rvSaleItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OnSaleItemAdapter(this, saleItemList);
        adapter.setOnRemoveClickListener(this);
        rvSaleItems.setAdapter(adapter);
    }

    private void loadMySaleItems() {
        executor.execute(() -> {
            try {
                // 获取当前用户ID
                int userId = BottomNavHelper.getUserId(OnSaleItemsActivity.this);
                Log.d(TAG, "加载用户ID: " + userId + " 的在售饰品");

                // 打开数据库连接
                itemForSaleDAO.open();
                userDAO.open();
                gameItemDAO.open();

                // 获取当前用户的在售饰品
                List<ItemForSale> items = itemForSaleDAO.getItemsForSaleByUserId(userId);
                Log.d(TAG, "查询到在售饰品数量: " + items.size());

                // 关联饰品和用户信息
                for (ItemForSale item : items) {
                    // 获取饰品详细信息
                    GameItem gameItem = gameItemDAO.getItemById(item.getItem_id());
                    if (gameItem != null) {
                        item.setGameItem(gameItem);
                    }

                    // 获取用户信息
                    User user = userDAO.getUserById(item.getUser_id());
                    if (user != null) {
                        item.setUser(user);
                    }
                }

                // 更新UI
                runOnUiThread(() -> {
                    saleItemList.clear();
                    saleItemList.addAll(items);
                    adapter.notifyDataSetChanged();

                    if (saleItemList.isEmpty()) {
                        rvSaleItems.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        rvSaleItems.setVisibility(View.VISIBLE);
                        tvEmpty.setVisibility(View.GONE);
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "加载在售饰品失败: " + e.getMessage(), e);
                runOnUiThread(() ->
                        Toast.makeText(this, "加载失败，请重试", Toast.LENGTH_SHORT).show());
            } finally {
                // 关闭数据库连接
                itemForSaleDAO.close();
                userDAO.close();
                gameItemDAO.close();
            }
        });
    }

    @Override
    public void onRemoveClick(int position, int saleId) {
        executor.execute(() -> {
            try {
                itemForSaleDAO.open();
                int result = itemForSaleDAO.deleteItemForSale(saleId);

                runOnUiThread(() -> {
                    if (result > 0) {
                        Toast.makeText(this, "已下架饰品", Toast.LENGTH_SHORT).show();
                        adapter.removeItem(position);

                        // 更新空状态
                        if (saleItemList.isEmpty()) {
                            rvSaleItems.setVisibility(View.GONE);
                            tvEmpty.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(this, "下架失败", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "下架失败: " + e.getMessage(), e);
                runOnUiThread(() ->
                        Toast.makeText(this, "操作失败", Toast.LENGTH_SHORT).show());
            } finally {
                itemForSaleDAO.close();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor = null;
    }
}