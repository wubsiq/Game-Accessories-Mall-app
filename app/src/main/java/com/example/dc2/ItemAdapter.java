package com.example.dc2;

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

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<GameItem> itemList;
    private OnItemClickListener listener;

    public ItemAdapter(List<GameItem> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        GameItem item = itemList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setItems(List<GameItem> items) {
        this.itemList = items;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivItemImage;
        private TextView tvItemId;
        private TextView tvItemName;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
            tvItemId = itemView.findViewById(R.id.tvItemId);
            tvItemName = itemView.findViewById(R.id.tvItemName);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(itemList.get(position));
                }
            });
        }

        public void bind(GameItem item) {
            tvItemId.setText("ID: " + item.getItem_id());
            tvItemName.setText(item.getItem_name());

            // 显示饰品图片
            if (item.getItem_image() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(
                        item.getItem_image(), 0, item.getItem_image().length);
                ivItemImage.setImageBitmap(bitmap);
            } else {
                ivItemImage.setImageResource(R.drawable.ic_item_placeholder);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(GameItem item);
    }
}