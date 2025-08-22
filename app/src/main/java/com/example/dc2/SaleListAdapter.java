package com.example.dc2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dc2.tabledao.*;
import com.example.dc2.table.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SaleListAdapter extends RecyclerView.Adapter<SaleListAdapter.ViewHolder> {

    private Context context;
    private List<ItemForSale> saleItems;
    private int itemId;
    private GameItemDAO gameItemDAO;
    private UserDAO userDAO;
    private ItemForSaleDAO itemForSaleDAO;
    private BuyRecordDAO buyRecordDAO;
    private SellRecordDAO sellRecordDAO;
    private OnPurchaseListener purchaseListener;

    public interface OnPurchaseListener {
        void onPurchaseSuccess(ItemForSale soldItem, double price);
        void onPurchaseFailure(String message);
    }

    public SaleListAdapter(Context context, List<ItemForSale> saleItems, int itemId,
                           OnPurchaseListener listener) {
        this.context = context;
        this.saleItems = saleItems;
        this.itemId = itemId;
        this.gameItemDAO = new GameItemDAO(context);
        this.userDAO = new UserDAO(context);
        this.itemForSaleDAO = new ItemForSaleDAO(context);
        this.buyRecordDAO = new BuyRecordDAO(context);
        this.sellRecordDAO = new SellRecordDAO(context);
        this.purchaseListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sale_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemForSale item = saleItems.get(position);

        // 获取饰品信息
        gameItemDAO.open();
        GameItem gameItem = gameItemDAO.getItemById(itemId);
        gameItemDAO.close();

        if (gameItem != null) {
            holder.tvItemName.setText(gameItem.getItem_name());
        } else {
            holder.tvItemName.setText("未知饰品");
        }

        holder.tvPrice.setText("价格: ¥" + String.format("%.2f", item.getPrice()));
        holder.tvSellerId.setText("卖家ID: " + item.getUser_id());

        // 点击购买
        holder.btnBuy.setOnClickListener(v -> {
            // 获取当前用户ID
            int buyerId = BottomNavHelper.getUserId(context);

            // 1. 检查是否购买自己的饰品
            if (item.getUser_id() == buyerId) {
                Toast.makeText(context, "不能购买自己的饰品", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. 获取买家钱包余额
            userDAO.open();
            User buyer = userDAO.getUserById(buyerId);
            userDAO.close();

            if (buyer == null) {
                Toast.makeText(context, "用户信息错误", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. 检查余额是否足够
            double price = item.getPrice();
            if (buyer.getWallet_balance() < price) {
                Toast.makeText(context, "余额不足，请充值", Toast.LENGTH_SHORT).show();
                return;
            }

            // 4. 执行交易
            executePurchase(item, buyer, price);
        });
    }

    private void executePurchase(ItemForSale item, User buyer, double price) {
        new Thread(() -> {
            try {
                // 打开所有DAO
                userDAO.open();
                gameItemDAO.open();
                itemForSaleDAO.open();
                buyRecordDAO.open();
                sellRecordDAO.open();

                // 1. 获取卖家信息
                User seller = userDAO.getUserById(item.getUser_id());
                if (seller == null) {
                    throw new Exception("卖家不存在");
                }

                // 2. 更新钱包余额
                // 买家扣款
                buyer.setWallet_balance(buyer.getWallet_balance() - price);
                userDAO.updateUser(buyer);

                // 卖家收款
                seller.setWallet_balance(seller.getWallet_balance() + price);
                userDAO.updateUser(seller);

                // 3. 删除卖家的购买记录（关键修改）
                deleteSellerBuyRecord(item.getItem_id(), seller.getUser_id());

                // 4. 添加买家的购买记录
                String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                BuyRecord buyRecord = new BuyRecord(
                        buyer.getUser_id(),
                        item.getItem_id(),
                        price,
                        currentTime
                );
                buyRecordDAO.addBuyRecord(buyRecord);

                // 5. 添加出售记录（卖家）
                SellRecord sellRecord = new SellRecord(
                        seller.getUser_id(),
                        item.getItem_id(),
                        price,
                        currentTime
                );
                sellRecordDAO.addSellRecord(sellRecord);

                // 6. 从在售列表中删除
                itemForSaleDAO.deleteItemForSale(item.getSale_id());

                // 通知购买成功
                runOnUiThread(() -> {
                    if (purchaseListener != null) {
                        purchaseListener.onPurchaseSuccess(item, price);
                    }
                    Toast.makeText(context, "购买成功！", Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                Log.e("PurchaseError", "购买失败: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    if (purchaseListener != null) {
                        purchaseListener.onPurchaseFailure(e.getMessage());
                    }
                    Toast.makeText(context, "购买失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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

    // 辅助方法：在后台线程运行UI操作
    private void runOnUiThread(Runnable action) {
        if (context instanceof android.app.Activity) {
            ((android.app.Activity) context).runOnUiThread(action);
        }
    }

    // 添加方法以更新数据
    public void setItems(List<ItemForSale> newItems) {
        this.saleItems = newItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return saleItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvPrice, tvSellerId;
        TextView btnBuy;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvSellerId = itemView.findViewById(R.id.tvSellerId);
            btnBuy = itemView.findViewById(R.id.btnBuy);
        }
    }
}