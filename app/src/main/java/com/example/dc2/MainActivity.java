package com.example.dc2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dc2.tabledao.GameItemDAO;
import com.example.dc2.table.GameItem;
import com.example.dc2.tabledao.UserDAO;
import com.example.dc2.table.User;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvGameItems;
    private GameItemAdapter gameItemAdapter;
    private List<GameItem> gameItems = new ArrayList<>();
    private EditText etSearch;
    private GameItemDAO gameItemDAO;
    private User currentUser;
    private ProgressBar progressBar;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 使用全局方法获取用户ID（双重保障）
        userId = BottomNavHelper.getUserId(this);
        if (userId == -1) {
            // 尝试从Intent获取
            userId = getIntent().getIntExtra("user_id", -1);

            if (userId == -1) {
                // 用户未登录，跳转到登录页面
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            } else {
                // 保存到全局
                BottomNavHelper.saveUserId(this, userId);
            }
        }

        // 初始化视图
        initViews();

        // 获取当前用户
        getCurrentUser();

        // 初始化数据库访问对象
        gameItemDAO = new GameItemDAO(this);

        // 设置RecyclerView
        setupRecyclerView();

        // 加载游戏饰品数据
        loadGameItems();

        // 设置搜索功能
        setupSearch();

        // 设置底部导航栏
        BottomNavHelper.setupBottomNavigation(this, R.id.nav_home);
    }

    private void initViews() {
        rvGameItems = findViewById(R.id.rvGameItems);
        etSearch = findViewById(R.id.etSearch);
        progressBar = findViewById(R.id.progressBar);

        // 搜索按钮点击事件
        findViewById(R.id.btnSearch).setOnClickListener(v -> performSearch());
    }

    private void getCurrentUser() {
        if (userId != -1) {
            UserDAO userDAO = new UserDAO(this);
            userDAO.open();
            currentUser = userDAO.getUserById(userId);
            userDAO.close();

            // 可选：显示用户名
            if (currentUser != null) {
                setTitle("欢迎，" + currentUser.getUsername());
            }
        }
    }

    private void setupRecyclerView() {
        gameItemAdapter = new GameItemAdapter(this, gameItems, userId); // 传递userId给适配器
        rvGameItems.setLayoutManager(new LinearLayoutManager(this));
        rvGameItems.setAdapter(gameItemAdapter);
    }

    private void loadGameItems() {
        // 显示加载进度
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            gameItemDAO.open();
            List<GameItem> items = gameItemDAO.getAllGameItemsWithPriceInfo();
            gameItemDAO.close();

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                if (items.isEmpty()) {
                    Toast.makeText(MainActivity.this, "没有找到饰品数据", Toast.LENGTH_SHORT).show();
                } else {
                    gameItems.clear();
                    gameItems.addAll(items);
                    gameItemAdapter.notifyDataSetChanged();
                }
            });
        }).start();
    }

    private void setupSearch() {
        // 搜索框文本变化监听
        etSearch.addTextChangedListener(new TextWatcher() {
            private Handler handler = new Handler();
            private Runnable runnable;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 取消之前的搜索请求
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }

                // 延迟500ms执行搜索，避免频繁请求
                runnable = () -> performSearch();
                handler.postDelayed(runnable, 500);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 键盘搜索按钮监听
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            performSearch();
            return true;
        });
    }

    private void performSearch() {
        String keyword = etSearch.getText().toString().trim();

        if (keyword.isEmpty()) {
            loadGameItems(); // 重新加载所有数据
            return;
        }

        // 显示加载进度
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            gameItemDAO.open();
            List<GameItem> searchResults = gameItemDAO.searchGameItemsWithPriceInfo(keyword);
            gameItemDAO.close();

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                if (searchResults.isEmpty()) {
                    Toast.makeText(MainActivity.this, "没有找到匹配的饰品", Toast.LENGTH_SHORT).show();
                } else {
                    gameItems.clear();
                    gameItems.addAll(searchResults);
                    gameItemAdapter.notifyDataSetChanged();
                }
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清理资源
        if (gameItemDAO != null) {
            gameItemDAO.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 更新用户信息（例如用户可能在个人资料页面更新了信息）
        getCurrentUser();
    }
}