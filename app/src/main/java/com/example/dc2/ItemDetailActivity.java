package com.example.dc2;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dc2.tabledao.BuyRecordDAO;
import com.example.dc2.tabledao.GameItemDAO;
import com.example.dc2.tabledao.ItemForSaleDAO;
import com.example.dc2.tabledao.ItemWantBuyDAO;
import com.example.dc2.tabledao.SellRecordDAO;
import com.example.dc2.tabledao.UserDAO;
import com.example.dc2.table.BuyRecord;
import com.example.dc2.table.GameItem;
import com.example.dc2.table.ItemForSale;
import com.example.dc2.table.ItemWantBuy;
import com.example.dc2.table.SellRecord;
import com.example.dc2.table.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// 1. 实现 OnPurchaseListener 接口
public class ItemDetailActivity extends AppCompatActivity
        implements SaleListAdapter.OnPurchaseListener {

    private int itemId;
    private GameItem gameItem;
    private GameItemDAO gameItemDAO;
    private ItemForSaleDAO itemForSaleDAO;
    private ItemWantBuyDAO itemWantBuyDAO;
    private BuyRecordDAO buyRecordDAO;
    private SellRecordDAO sellRecordDAO; // 新增
    private UserDAO userDAO;
    private User currentUser;
    private int userId;

    // 视图组件
    private ImageView ivItemImage;
    private TextView tvItemName;
    private RecyclerView rvSaleList;
    private SaleListAdapter saleListAdapter;
    private List<ItemForSale> saleItems = new ArrayList<>();
    private ProgressBar progressBar;
    private Button btnBuyNow;
    private Button btnWantBuy;
    private RecyclerView rvWantBuyList;
    private WantBuyListAdapter wantBuyListAdapter;
    private List<ItemWantBuy> wantBuyItems = new ArrayList<>();
    private BuyRecord[] userItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // 获取传递的item_id
        itemId = getIntent().getIntExtra("item_id", -1);
        if (itemId == -1) {
            Toast.makeText(this, "参数错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 初始化DAO
        gameItemDAO = new GameItemDAO(this);
        itemForSaleDAO = new ItemForSaleDAO(this);
        itemWantBuyDAO = new ItemWantBuyDAO(this);
        buyRecordDAO = new BuyRecordDAO(this);
        userDAO = new UserDAO(this);
        sellRecordDAO = new SellRecordDAO(this); // 新增

        // 获取用户ID
        userId = BottomNavHelper.getUserId(this);
        if (userId == -1) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // 获取当前用户
        userDAO.open();
        currentUser = userDAO.getUserById(userId);
        userDAO.close();

        // 初始化视图
        initViews();

        // 加载饰品详情
        loadItemDetails();

        // 加载在售列表
        loadSaleItems();

        // 加载求购列表
        loadWantBuyItems();

        // 设置按钮点击事件
        setupButtons();
    }

    private void initViews() {
        ivItemImage = findViewById(R.id.ivItemImage);
        tvItemName = findViewById(R.id.tvItemName);
        rvSaleList = findViewById(R.id.rvSaleList);
        rvWantBuyList = findViewById(R.id.rvWantBuyList);
        progressBar = findViewById(R.id.progressBar);
        btnBuyNow = findViewById(R.id.btnBuyNow);
        btnWantBuy = findViewById(R.id.btnWantBuy);

        // 设置返回按钮
        findViewById(R.id.ivBack).setOnClickListener(v -> onBackPressed());

        // 设置RecyclerView
        rvSaleList.setLayoutManager(new LinearLayoutManager(this));

        // 2. 正确初始化适配器
        saleListAdapter = new SaleListAdapter(
                this,
                saleItems,
                itemId,
                this // 传递实现了接口的Activity
        );

        rvSaleList.setAdapter(saleListAdapter);

        rvWantBuyList.setLayoutManager(new LinearLayoutManager(this));
        wantBuyListAdapter = new WantBuyListAdapter(this, wantBuyItems);
        rvWantBuyList.setAdapter(wantBuyListAdapter);
    }

    private void loadItemDetails() {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            gameItemDAO.open();
            gameItem = gameItemDAO.getItemWithPriceInfo(itemId);
            gameItemDAO.close();

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                if (gameItem != null) {
                    tvItemName.setText(gameItem.getItem_name());
                    // 加载图片
                    if (gameItem.getItem_image() != null && gameItem.getItem_image().length > 0) {
                        Glide.with(this)
                                .asBitmap()
                                .load(gameItem.getItem_image())
                                .placeholder(R.drawable.placeholder_image)
                                .error(R.drawable.error_image)
                                .into(ivItemImage);
                    } else {
                        ivItemImage.setImageResource(R.drawable.placeholder_image);
                    }
                } else {
                    Toast.makeText(this, "获取饰品信息失败", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }).start();
    }

    private void loadSaleItems() {
        new Thread(() -> {
            itemForSaleDAO.open();
            List<ItemForSale> items = itemForSaleDAO.getItemsForSaleByItemId(itemId);
            itemForSaleDAO.close();

            runOnUiThread(() -> {
                saleItems.clear();
                saleItems.addAll(items);
                if (saleListAdapter != null) { // 添加空检查
                    saleListAdapter.setItems(saleItems);
                    saleListAdapter.notifyDataSetChanged();
                }
            });
        }).start();
    }

    private void loadWantBuyItems() {
        new Thread(() -> {
            itemWantBuyDAO.open();
            List<ItemWantBuy> items = itemWantBuyDAO.getItemsWantBuyByItemId(itemId);
            itemWantBuyDAO.close();

            runOnUiThread(() -> {
                wantBuyItems.clear();
                wantBuyItems.addAll(items);
                wantBuyListAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    private void setupButtons() {
        // 立即购买按钮 - 现在购买第一个商品
        btnBuyNow.setOnClickListener(v -> {
            if (saleItems.isEmpty()) {
                Toast.makeText(this, "暂无在售商品", Toast.LENGTH_SHORT).show();
                return;
            }

            // 购买列表中的第一个商品
            ItemForSale firstItem = saleItems.get(0);

            // 获取当前用户
            userDAO.open();
            User buyer = userDAO.getUserById(userId);
            userDAO.close();

            if (buyer == null) {
                Toast.makeText(this, "用户信息错误", Toast.LENGTH_SHORT).show();
                return;
            }

            // 执行购买
            executePurchase(firstItem, buyer);
        });

        // 求购按钮
        btnWantBuy.setOnClickListener(v -> showWantBuyDialog());
    }

    // 3. 实现 OnPurchaseListener 接口方法
    @Override
    public void onPurchaseSuccess(ItemForSale soldItem, double price) {
        // 购买成功后刷新列表
        loadSaleItems();

        // 更新用户余额显示
        updateUserBalance();
    }

    @Override
    public void onPurchaseFailure(String message) {
        Toast.makeText(this, "购买失败: " + message, Toast.LENGTH_SHORT).show();
    }

    private void updateUserBalance() {
        userDAO.open();
        User user = userDAO.getUserById(userId);
        userDAO.close();

        // 更新钱包显示（如果有相关UI）
        // 例如：tvWalletBalance.setText("¥" + user.getWallet_balance());
    }

    private void executePurchase(ItemForSale saleItem, User buyer) {
        new Thread(() -> {
            try {
                // 打开所有DAO
                userDAO.open();
                gameItemDAO.open();
                itemForSaleDAO.open();
                buyRecordDAO.open();
                sellRecordDAO.open();

                // 1. 获取卖家信息
                User seller = userDAO.getUserById(saleItem.getUser_id());
                if (seller == null) {
                    throw new Exception("卖家不存在");
                }

                double price = saleItem.getPrice();

                // 2. 检查余额
                if (buyer.getWallet_balance() < price) {
                    throw new Exception("余额不足");
                }

                // 3. 更新钱包余额
                // 买家扣款
                buyer.setWallet_balance(buyer.getWallet_balance() - price);
                userDAO.updateUser(buyer);

                // 卖家收款
                seller.setWallet_balance(seller.getWallet_balance() + price);
                userDAO.updateUser(seller);

                // 4. 删除卖家的购买记录（关键修改）
                deleteSellerBuyRecord(saleItem.getItem_id(), seller.getUser_id());

                // 5. 添加买家的购买记录
                String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                BuyRecord buyRecord = new BuyRecord(
                        buyer.getUser_id(),
                        saleItem.getItem_id(),
                        price,
                        currentTime
                );
                buyRecordDAO.addBuyRecord(buyRecord);

                // 6. 添加出售记录（卖家）
                SellRecord sellRecord = new SellRecord(
                        seller.getUser_id(),
                        saleItem.getItem_id(),
                        price,
                        currentTime
                );
                sellRecordDAO.addSellRecord(sellRecord);

                // 7. 从在售列表中删除
                itemForSaleDAO.deleteItemForSale(saleItem.getSale_id());

                runOnUiThread(() -> {
                    Toast.makeText(ItemDetailActivity.this, "购买成功", Toast.LENGTH_SHORT).show();
                    loadSaleItems();
                    updateUserBalance();
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(ItemDetailActivity.this, "购买失败: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            } finally {
                // 关闭所有DAO
                userDAO.close();
                gameItemDAO.close();
                itemForSaleDAO.close();
                buyRecordDAO.close();
                sellRecordDAO.close();
            }
        }).start();
    }

    // 新增方法：删除卖家的购买记录
    private void deleteSellerBuyRecord(int itemId, int sellerId) throws Exception {
        // 获取卖家的购买记录
        List<BuyRecord> sellerRecords = buyRecordDAO.getBuyRecordsByUserId(sellerId, false);

        // 查找匹配的购买记录
        BuyRecord recordToDelete = null;
        for (BuyRecord record : sellerRecords) {
            if (record.getItem_id() == itemId) {
                recordToDelete = record;
                break;
            }
        }

        if (recordToDelete == null) {
            throw new Exception("卖家没有该饰品的购买记录");
        }

        // 删除购买记录
        buyRecordDAO.deleteBuyRecord(recordToDelete.getRecord_id());
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void showWantBuyDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_want_buy, null);
        final EditText etPrice = view.findViewById(R.id.etPrice);
        final EditText etQuantity = view.findViewById(R.id.etQuantity);

        new AlertDialog.Builder(this)
                .setTitle("我要求购")
                .setView(view)
                .setPositiveButton("确定", (dialog, which) -> {
                    String priceStr = etPrice.getText().toString().trim();
                    String quantityStr = etQuantity.getText().toString().trim();

                    if (priceStr.isEmpty() || quantityStr.isEmpty()) {
                        Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double price = Double.parseDouble(priceStr);
                        int quantity = Integer.parseInt(quantityStr);

                        // 保存求购记录
                        saveWantBuyRecord(price, quantity);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "请输入正确的数字", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void saveWantBuyRecord(double price, int quantity) {
        new Thread(() -> {
            ItemWantBuy wantBuy = new ItemWantBuy();
            wantBuy.setItem_id(itemId);
            wantBuy.setUser_id(userId);
            wantBuy.setWant_price(price);
            wantBuy.setQuantity(quantity);

            itemWantBuyDAO.open();
            itemWantBuyDAO.addItemWantBuy(wantBuy);
            itemWantBuyDAO.close();

            runOnUiThread(() -> {
                Toast.makeText(this, "求购信息已提交", Toast.LENGTH_SHORT).show();
                // 刷新求购列表
                loadWantBuyItems();
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭DAO
        if (gameItemDAO != null) gameItemDAO.close();
        if (itemForSaleDAO != null) itemForSaleDAO.close();
        if (itemWantBuyDAO != null) itemWantBuyDAO.close();
        if (buyRecordDAO != null) buyRecordDAO.close();
        if (userDAO != null) userDAO.close();
        if (sellRecordDAO != null) sellRecordDAO.close(); // 新增
    }
}