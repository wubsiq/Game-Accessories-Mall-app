package com.example.dc2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dc2.tabledao.UserDAO;
import com.example.dc2.table.User;

import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity {

    private RecyclerView rvUsers;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private EditText etSearch;
    private UserDAO userDAO;
    private ProgressBar progressBar;
    private int currentUserId; // 当前操作的用户ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        // 初始化视图
        rvUsers = findViewById(R.id.rvUsers);
        etSearch = findViewById(R.id.etSearch);
        progressBar = findViewById(R.id.progressBar);

        // 初始化数据库操作对象
        userDAO = new UserDAO(this);

        // 设置RecyclerView
        userAdapter = new UserAdapter(this, userList, this::onUserClick);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(userAdapter);

        // 加载所有用户数据
        loadAllUsers();

        // 设置搜索功能
        setupSearch();
    }

    /**
     * 加载所有用户数据
     */
    private void loadAllUsers() {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            userDAO.open();
            List<User> users = userDAO.getAllUsers();
            userDAO.close();

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                userList.clear();
                userList.addAll(users);
                userAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    /**
     * 设置搜索功能
     */
    private void setupSearch() {
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
                runnable = () -> searchUsers(s.toString().trim());
                handler.postDelayed(runnable, 500);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * 搜索用户（支持ID、用户名、手机号复合查询）
     */
    private void searchUsers(String keyword) {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            userDAO.open();
            List<User> searchResults = new ArrayList<>();

            if (keyword.isEmpty()) {
                searchResults = userDAO.getAllUsers();
            } else {
                try {
                    int userId = Integer.parseInt(keyword);
                    User user = userDAO.getUserById(userId);
                    if (user != null) searchResults.add(user);
                } catch (NumberFormatException e) {
                    List<User> usersByUsername = userDAO.getAllUsers();
                    for (User user : usersByUsername) {
                        if (user.getUsername().contains(keyword) ||
                                user.getPhone().contains(keyword)) {
                            searchResults.add(user);
                        }
                    }
                }
            }

            userDAO.close();

            // 创建临时final变量
            final List<User> finalResults = searchResults;

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                userList.clear();
                userList.addAll(finalResults);
                userAdapter.notifyDataSetChanged();

                if (finalResults.isEmpty()) {
                    Toast.makeText(UserManagementActivity.this, "未找到匹配的用户", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    /**
     * 用户点击事件处理
     */
    private void onUserClick(User user) {
        currentUserId = user.getUser_id();
        showUserOptionsDialog(user);
    }

    /**
     * 显示用户操作选项对话框
     */
    private void showUserOptionsDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("用户操作")
                .setItems(new CharSequence[]{"修改", "删除"}, (dialog, which) -> {
                    switch (which) {
                        case 0: // 修改
                            showEditUserDialog(user);
                            break;
                        case 1: // 删除
                            confirmDeleteUser(user);
                            break;
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 显示编辑用户对话框
     */
    private void showEditUserDialog(User user) {
        EditUserDialog dialog = new EditUserDialog(this, user, (updatedUser) -> {
            updateUser(updatedUser);
            return true;
        });
        dialog.show();
    }

    /**
     * 更新用户信息
     */
    private void updateUser(User user) {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            userDAO.open();
            int result = userDAO.updateUser(user);
            userDAO.close();

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                if (result > 0) {
                    Toast.makeText(UserManagementActivity.this, "用户信息更新成功", Toast.LENGTH_SHORT).show();
                    loadAllUsers(); // 刷新数据
                } else {
                    Toast.makeText(UserManagementActivity.this, "用户信息更新失败", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    /**
     * 确认删除用户
     */
    private void confirmDeleteUser(User user) {
        new AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除用户 " + user.getUsername() + " 吗？")
                .setPositiveButton("删除", (dialog, which) -> deleteUser(user.getUser_id()))
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 删除用户
     */
    private void deleteUser(int userId) {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            userDAO.open();
            int result = userDAO.deleteUser(userId);
            userDAO.close();

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                if (result > 0) {
                    Toast.makeText(UserManagementActivity.this, "用户删除成功", Toast.LENGTH_SHORT).show();
                    loadAllUsers(); // 刷新数据
                } else {
                    Toast.makeText(UserManagementActivity.this, "用户删除失败", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}