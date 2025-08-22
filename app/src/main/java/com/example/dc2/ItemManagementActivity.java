package com.example.dc2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dc2.tabledao.GameItemDAO;
import com.example.dc2.table.GameItem;

import java.util.List;

public class ItemManagementActivity extends AppCompatActivity {

    private RecyclerView rvItems;
    private ItemAdapter adapter;
    private GameItemDAO gameItemDAO;
    private ProgressBar progressBar;
    private Button btnAddItem;
    private List<GameItem> gameItemList;
    private AddEditItemDialog currentDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_management);

        // 初始化视图组件
        rvItems = findViewById(R.id.rvItems);
        progressBar = findViewById(R.id.progressBar);
        btnAddItem = findViewById(R.id.btnAddItem);

        // 设置RecyclerView
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        gameItemList = java.util.Collections.emptyList();
        adapter = new ItemAdapter(gameItemList, this::onItemClick);
        rvItems.setAdapter(adapter);

        // 新增饰品按钮点击事件
        btnAddItem.setOnClickListener(v -> showAddItemDialog());

        // 初始化数据库操作对象
        gameItemDAO = new GameItemDAO(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 每次页面可见时加载数据
        loadGameItems();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AddEditItemDialog.PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null) {

            Log.d("ItemManagementActivity", "onActivityResult: requestCode=" + requestCode +
                    ", resultCode=" + resultCode +
                    ", data=" + data.getData());

            Uri imageUri = data.getData();
            if (currentDialog != null && imageUri != null) {
                currentDialog.setSelectedImage(imageUri);
            } else {
                Toast.makeText(this, "图片选择失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 加载饰品数据
     */
    private void loadGameItems() {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            gameItemDAO.open();
            gameItemList = gameItemDAO.getAllGameItems();
            gameItemDAO.close();

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                adapter.setItems(gameItemList);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    /**
     * 饰品项点击事件处理
     */
    private void onItemClick(GameItem item) {
        // 显示操作对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择操作")
                .setItems(new CharSequence[]{"修改", "删除"}, (dialog, which) -> {
                    switch (which) {
                        case 0: // 修改
                            showEditItemDialog(item);
                            break;
                        case 1: // 删除
                            confirmDeleteItem(item);
                            break;
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 显示新增饰品对话框
     */
    // 在 showAddItemDialog 和 showEditItemDialog 中设置 currentDialog
    private void showAddItemDialog() {
        AddEditItemDialog dialog = new AddEditItemDialog(this, null,
                (name, image) -> {
                    addNewItem(name, image);
                    return true;
                });

        // 设置当前对话框并添加关闭监听
        currentDialog = dialog;
        dialog.setOnDismissListener(dialog1 -> currentDialog = null);
        dialog.show();
    }

    private void showEditItemDialog(GameItem item) {
        AddEditItemDialog dialog = new AddEditItemDialog(this, item,
                (name, image) -> {
                    updateItem(item.getItem_id(), name, image);
                    return true;
                });

        // 设置当前对话框并添加关闭监听
        currentDialog = dialog;
        dialog.setOnDismissListener(dialog1 -> currentDialog = null);
        dialog.show();
    }

    /**
     * 添加新饰品
     */
    private void addNewItem(String name, byte[] image) {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            gameItemDAO.open();

            GameItem newItem = new GameItem();
            newItem.setItem_name(name);
            newItem.setItem_image(image);

            long result = gameItemDAO.addGameItem(newItem);
            gameItemDAO.close();

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                if (result > 0) {
                    Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
                    loadGameItems(); // 刷新数据
                } else {
                    Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    /**
     * 更新饰品信息
     */
    private void updateItem(int itemId, String name, byte[] image) {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            gameItemDAO.open();

            GameItem item = gameItemDAO.getItemById(itemId);
            if (item != null) {
                item.setItem_name(name);
                if (image != null) {
                    item.setItem_image(image);
                }
                gameItemDAO.updateGameItem(item);
            }

            gameItemDAO.close();

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
                loadGameItems(); // 刷新数据
            });
        }).start();
    }

    /**
     * 确认删除饰品
     */
    private void confirmDeleteItem(GameItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认删除")
                .setMessage("确定要删除该饰品吗？")
                .setPositiveButton("删除", (dialog, which) -> deleteItem(item.getItem_id()))
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 删除饰品
     */
    private void deleteItem(int itemId) {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            gameItemDAO.open();

            // 先检查是否有在售记录
            int onSaleCount = gameItemDAO.getOnSaleCountForItem(itemId);
            if (onSaleCount > 0) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "该饰品有在售记录，无法删除", Toast.LENGTH_SHORT).show();
                });
                gameItemDAO.close();
                return;
            }

            int result = gameItemDAO.deleteGameItem(itemId);
            gameItemDAO.close();

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                if (result > 0) {
                    Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                    loadGameItems(); // 刷新数据
                } else {
                    Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}