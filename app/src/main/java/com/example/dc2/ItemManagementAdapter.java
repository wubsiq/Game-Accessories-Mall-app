package com.example.dc2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dc2.table.GameItem;

import java.util.List;

public class ItemManagementAdapter extends RecyclerView.Adapter<ItemManagementAdapter.ViewHolder> {

    private Context context;
    private List<GameItem> gameItems;
    private OnItemClickListener listener;

    public ItemManagementAdapter(Context context, List<GameItem> gameItems, OnItemClickListener listener) {
        this.context = context;
        this.gameItems = gameItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_management_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GameItem item = gameItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return gameItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItemImage;
        TextView tvItemName, tvItemId, tvPrice, tvOnSaleCount;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
            tvItemId = itemView.findViewById(R.id.tvItemId);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvOnSaleCount = itemView.findViewById(R.id.tvOnSaleCount);

            // 点击事件
            itemView.setOnClickListener(v -> {
                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(gameItems.get(getAdapterPosition()));
                }
            });
        }

        void bind(GameItem item) {
            // 显示饰品ID
            tvItemId.setText("ID: " + item.getItem_id());
            // 显示饰品名称
            tvItemName.setText(item.getItem_name());
            // 显示最低价格
            tvPrice.setText("最低价格: ¥" + item.getMin_price());
            // 显示在售数量
            tvOnSaleCount.setText("在售数量: " + item.getOn_sale_count());
            // 显示饰品图片
            if (item.getItem_image() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(
                        item.getItem_image(), 0, item.getItem_image().length);
                ivItemImage.setImageBitmap(bitmap);
            }
        }
    }

    interface OnItemClickListener {
        void onItemClick(GameItem item);
    }
}