package com.example.dc2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dc2.table.ItemForSale;
import com.example.dc2.tabledao.ItemForSaleDAO;

import java.util.List;

public class OnSaleItemAdapter extends RecyclerView.Adapter<OnSaleItemAdapter.ViewHolder> {

    private Context context;
    private List<ItemForSale> saleItemList;
    private OnRemoveClickListener listener;

    public interface OnRemoveClickListener {
        void onRemoveClick(int position, int saleId);
    }

    public void setOnRemoveClickListener(OnRemoveClickListener listener) {
        this.listener = listener;
    }

    public OnSaleItemAdapter(Context context, List<ItemForSale> saleItemList) {
        this.context = context;
        this.saleItemList = saleItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_my_sale, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemForSale saleItem = saleItemList.get(position);

        // 显示饰品名称
        holder.tvItemName.setText(saleItem.getGameItem().getItem_name());

        // 显示价格
        holder.tvPrice.setText(String.format("¥%.2f", saleItem.getPrice()));

        // 设置下架按钮点击事件
        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveClick(position, saleItem.getSale_id());
            }
        });
    }

    @Override
    public int getItemCount() {
        return saleItemList.size();
    }

    public void removeItem(int position) {
        saleItemList.remove(position);
        notifyItemRemoved(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvPrice;
        Button btnRemove;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}