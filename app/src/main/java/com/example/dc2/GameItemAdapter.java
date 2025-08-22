package com.example.dc2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.dc2.table.GameItem;

import java.util.List;

public class GameItemAdapter extends RecyclerView.Adapter<GameItemAdapter.ViewHolder> {

    private final Context context;
    private final List<GameItem> gameItems;

    public GameItemAdapter(Context context, List<GameItem> gameItems, int userId) {
        this.context = context;
        this.gameItems = gameItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GameItem item = gameItems.get(position);

        // 设置饰品名称
        holder.tvItemName.setText(item.getItem_name());

        // 设置饰品价格 - 显示最低价格
        if (item.getMin_price() > 0) {
            holder.tvItemPrice.setText(String.format("¥ %.2f", item.getMin_price()));
        } else {
            holder.tvItemPrice.setText("暂无在售");
        }

        // 设置在售数量
        holder.tvOnSaleCount.setText(String.format("在售: %d", item.getOn_sale_count()));

        // 使用Glide加载图片
        if (item.getItem_image() != null && item.getItem_image().length > 0) {
            Glide.with(context)
                    .asBitmap()
                    .load(item.getItem_image())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(holder.ivItemImage);
        } else {
            holder.ivItemImage.setImageResource(R.drawable.placeholder_image);
        }

        // 设置点击事件
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDetailActivity.class);
            intent.putExtra("item_id", item.getItem_id());
            context.startActivity(intent);
        });

        // 立即购买按钮点击事件
        holder.btnBuy.setOnClickListener(v -> {
            if (item.getOn_sale_count() > 0) {
                // 跳转到购买页面
                Intent buyIntent = new Intent(context, ItemDetailActivity.class);
                buyIntent.putExtra("item_id", item.getItem_id());
                context.startActivity(buyIntent);
            } else {
                Toast.makeText(context, "该饰品暂无在售", Toast.LENGTH_SHORT).show();
            }
        });

        // 根据在售状态设置按钮可用性
        holder.btnBuy.setEnabled(item.getOn_sale_count() > 0);
    }

    @Override
    public int getItemCount() {
        return gameItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivItemImage;
        TextView tvItemName;
        TextView tvItemPrice;
        TextView tvOnSaleCount;
        Button btnBuy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            tvOnSaleCount = itemView.findViewById(R.id.tvOnSaleCount);
            btnBuy = itemView.findViewById(R.id.btnBuy);
        }
    }

    // 更新数据
    public void updateData(List<GameItem> newItems) {
        gameItems.clear();
        gameItems.addAll(newItems);
        notifyDataSetChanged();
    }

    // 添加更多数据（用于分页加载）
    public void addMoreData(List<GameItem> moreItems) {
        int startPosition = gameItems.size();
        gameItems.addAll(moreItems);
        notifyItemRangeInserted(startPosition, moreItems.size());
    }
}