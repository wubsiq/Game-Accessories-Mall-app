package com.example.dc2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dc2.tabledao.GameItemDAO;
import com.example.dc2.tabledao.ItemWantBuyDAO;
import com.example.dc2.table.GameItem;
import com.example.dc2.table.ItemWantBuy;

import java.util.ArrayList;
import java.util.List;

public class BuyActivity extends AppCompatActivity {

    private RecyclerView rvBuyList;
    private ProgressBar progressBar;
    private TextView tvEmptyList;
    private ItemWantBuyDAO itemWantBuyDAO;
    private GameItemDAO gameItemDAO;
    private int userId;
    private BuyListAdapter adapter;
    private List<ItemWantBuy> wantBuyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buy);
// 使用全局方法获取用户ID
        userId = BottomNavHelper.getUserId(this);

        if (userId == -1) {
            Toast.makeText(this, "用户未登录，请重新登录", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        // 设置底部导航栏2
        BottomNavHelper.setupBottomNavigation(this, R.id.nav_buy);

        // 初始化视图
        rvBuyList = findViewById(R.id.rvBuyList);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyList = findViewById(R.id.tvEmptyList);

        // 初始化DAO
        itemWantBuyDAO = new ItemWantBuyDAO(this);
        gameItemDAO = new GameItemDAO(this);

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

        // 初始化RecyclerView
        initRecyclerView();

        // 加载求购记录
        loadBuyList();

        // 设置窗口内边距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        rvBuyList.setLayoutManager(new LinearLayoutManager(this));
        wantBuyList = new ArrayList<>();
        adapter = new BuyListAdapter(this, wantBuyList, gameItemDAO);
        rvBuyList.setAdapter(adapter);
    }

    /**
     * 加载求购记录
     */
    private void loadBuyList() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyList.setVisibility(View.GONE);

        new Thread(() -> {
            itemWantBuyDAO.open();
            // 获取当前用户的求购记录（按求购价格降序排列）
            List<ItemWantBuy> list = itemWantBuyDAO.getItemsWantBuyByUserId(userId);
            itemWantBuyDAO.close();

            // 在主线程更新UI
            new Handler(Looper.getMainLooper()).post(() -> {
                progressBar.setVisibility(View.GONE);
                if (list != null && !list.isEmpty()) {
                    wantBuyList.clear();
                    wantBuyList.addAll(list);
                    adapter.notifyDataSetChanged();
                } else {
                    tvEmptyList.setVisibility(View.VISIBLE);
                }
            });
        }).start();
    }

    /**
     * 刷新求购列表
     */
    public void refreshBuyList() {
        loadBuyList();
    }
}