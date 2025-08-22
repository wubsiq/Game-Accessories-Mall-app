package com.example.dc2;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dc2.table.BuyRecord;
import com.example.dc2.table.GameItem;
import com.example.dc2.table.User;

import java.util.List;
import java.util.Map;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private Context context;
    private List<BuyRecord> buyRecordList;
    private Map<Integer, GameItem> itemMap;

    // 添加点击回调接口
    public interface OnItemClickListener {
        void onItemClick(BuyRecord record, GameItem item);
    }

    private OnItemClickListener listener;

    // 添加设置监听器方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public InventoryAdapter(Context context, List<BuyRecord> buyRecordList, Map<Integer, GameItem> itemMap) {
        this.context = context;
        this.buyRecordList = buyRecordList;
        this.itemMap = itemMap;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        BuyRecord record = buyRecordList.get(position);
        GameItem item = itemMap.get(record.getItem_id());

        if (item != null) {
            holder.bind(record, item);
        }
    }

    @Override
    public int getItemCount() {
        return buyRecordList.size();
    }

    class InventoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItemImage;
        TextView tvItemName, tvPurchasePrice;

        InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvPurchasePrice = itemView.findViewById(R.id.tvPurchasePrice);
        }

        void bind(BuyRecord record, GameItem item) {
            tvItemName.setText(item.getItem_name());
            tvPurchasePrice.setText("¥" + String.format("%.2f", record.getPurchase_price()));

            // 加载饰品图片
            if (item.getItem_image() != null) {
                Bitmap bitmap = User.byteArrayToBitmap(item.getItem_image());
                Glide.with(context)
                        .load(bitmap)
                        .into(ivItemImage);
            } else {
                // 默认图片
                ivItemImage.setImageResource(R.drawable.default_item_image);
            }

            // 添加点击事件
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(record, item);
                }
            });
        }
    }
}