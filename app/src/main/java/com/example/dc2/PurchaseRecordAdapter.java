package com.example.dc2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dc2.table.BuyRecord;
import com.example.dc2.table.GameItem;
import com.example.dc2.table.User;

import java.util.List;

public class PurchaseRecordAdapter extends RecyclerView.Adapter<PurchaseRecordAdapter.ViewHolder> {

    private Context context;
    private List<BuyRecord> recordList;

    public PurchaseRecordAdapter(Context context, List<BuyRecord> recordList) {
        this.context = context;
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_purchase_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BuyRecord record = recordList.get(position);

        // 显示记录ID
        holder.tvRecordId.setText("记录ID: " + record.getRecord_id());

        // 显示用户信息
        User user = record.getUser();
        if (user != null) {
            holder.tvUserId.setText("用户ID: " + user.getUser_id());
            holder.tvUsername.setText("用户名: " + user.getUsername());
        } else {
            holder.tvUserId.setText("用户ID: 未知");
            holder.tvUsername.setText("用户名: 未知");
        }

        // 显示商品信息
        GameItem gameItem = record.getGameItem();
        if (gameItem != null) {
            holder.tvItemId.setText("商品ID: " + gameItem.getItem_id());
            holder.tvItemName.setText("商品名称: " + gameItem.getItem_name());
        } else {
            holder.tvItemId.setText("商品ID: 未知");
            holder.tvItemName.setText("商品名称: 未知");
        }

        // 显示购买价格和时间
        holder.tvPurchasePrice.setText("购买价格: ¥" + String.format("%.2f", record.getPurchase_price()));
        holder.tvPurchaseTime.setText("购买时间: " + record.getPurchase_time());
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRecordId, tvUserId, tvUsername, tvItemId, tvItemName, tvPurchasePrice, tvPurchaseTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRecordId = itemView.findViewById(R.id.tvRecordId);
            tvUserId = itemView.findViewById(R.id.tvUserId);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvItemId = itemView.findViewById(R.id.tvItemId);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvPurchasePrice = itemView.findViewById(R.id.tvPurchasePrice);
            tvPurchaseTime = itemView.findViewById(R.id.tvPurchaseTime);
        }
    }
}