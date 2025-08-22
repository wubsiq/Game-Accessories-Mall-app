package com.example.dc2;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.dc2.table.BuyRecord;
import com.example.dc2.tabledao.BuyRecordDAO;
import com.example.dc2.tabledao.GameItemDAO;
import com.example.dc2.tabledao.ItemForSaleDAO;
import com.example.dc2.table.GameItem;
import com.example.dc2.table.ItemForSale;
import com.example.dc2.table.User;

public class PublishSaleActivity extends AppCompatActivity {

    private ImageView ivItemImage;
    private TextView tvItemName, tvMarketPrice;
    private EditText etSalePrice;
    private Button btnSave;

    private int userId;
    private int itemId;
    private int buyRecordId;
    private String itemName;
    private double purchasePrice;
    private byte[] itemImageBytes;

    private GameItemDAO gameItemDAO;
    private ItemForSaleDAO itemForSaleDAO;
    private BuyRecordDAO buyRecordDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_sale);

        // 启用ActionBar的返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("饰品上架");
        }

        // 初始化视图
        ivItemImage = findViewById(R.id.ivItemImage);
        tvItemName = findViewById(R.id.tvItemName);
        tvMarketPrice = findViewById(R.id.tvMarketPrice);
        etSalePrice = findViewById(R.id.etSalePrice);
        btnSave = findViewById(R.id.btnSave);

        // 获取传递的数据
        buyRecordId = getIntent().getIntExtra("BUY_RECORD_ID", -1);
        itemId = getIntent().getIntExtra("ITEM_ID", -1);
        itemName = getIntent().getStringExtra("ITEM_NAME");
        purchasePrice = getIntent().getDoubleExtra("PURCHASE_PRICE", 0.0);
        itemImageBytes = getIntent().getByteArrayExtra("ITEM_IMAGE");

        // 使用全局方法获取用户ID
        userId = BottomNavHelper.getUserId(this);

        // 初始化DAO
        gameItemDAO = new GameItemDAO(this);
        itemForSaleDAO = new ItemForSaleDAO(this);
        buyRecordDAO=new BuyRecordDAO(this);

        // 显示饰品信息
        displayItemInfo();

        // 获取并显示市场最低价格
        loadMarketPrice();

        // 设置保存按钮点击事件
        btnSave.setOnClickListener(v -> saveSaleItem());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // 返回按钮关闭当前Activity
        return true;
    }

    /**
     * 显示饰品信息
     */
    private void displayItemInfo() {
        if (itemName != null) {
            tvItemName.setText(itemName);
        }

        if (itemImageBytes != null) {
            Bitmap bitmap = User.byteArrayToBitmap(itemImageBytes);
            Glide.with(this)
                    .load(bitmap)
                    .placeholder(R.drawable.default_item_image)
                    .error(R.drawable.default_item_image)
                    .into(ivItemImage);
        } else {
            ivItemImage.setImageResource(R.drawable.default_item_image);
        }
    }

    /**
     * 加载市场最低价格
     */
    private void loadMarketPrice() {
        new Thread(() -> {
            gameItemDAO.open();
            double minPrice = gameItemDAO.getMinPriceForItem(itemId);
            gameItemDAO.close();

            runOnUiThread(() -> {
                if (minPrice > 0) {
                    tvMarketPrice.setText("市场最低价: ¥" + String.format("%.2f", minPrice));
                } else {
                    tvMarketPrice.setText("暂无市场参考价");
                }
            });
        }).start();
    }

    /**
     * 保存出售物品
     */
    private void saveSaleItem() {
        String priceStr = etSalePrice.getText().toString().trim();
        if (priceStr.isEmpty()) {
            etSalePrice.setError("请输入出售价格");
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                etSalePrice.setError("价格必须大于0");
                return;
            }

            // 显示加载提示
            Toast.makeText(this, "正在上架...", Toast.LENGTH_SHORT).show();

            // 在后台线程执行数据库操作
            new Thread(() -> {
                itemForSaleDAO.open();

                // 创建出售物品
                ItemForSale saleItem = new ItemForSale();
                saleItem.setItem_id(itemId);
                saleItem.setUser_id(userId);
                saleItem.setPrice(price);

                long result = itemForSaleDAO.addItemForSale(saleItem);
                itemForSaleDAO.close();

                runOnUiThread(() -> {
                    if (result != -1) {
                        Toast.makeText(this, "上架成功！", Toast.LENGTH_SHORT).show();
                        finish(); // 关闭当前页面
                    } else {
                        Toast.makeText(this, "上架失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();

        } catch (NumberFormatException e) {
            etSalePrice.setError("请输入有效数字");
        }
    }
}