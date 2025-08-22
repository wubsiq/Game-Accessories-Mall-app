package com.example.dc2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dc2.tabledao.GameItemDAO;
import com.example.dc2.table.GameItem;
import com.example.dc2.table.ItemWantBuy;

import java.util.List;

public class WantBuyListAdapter extends RecyclerView.Adapter<WantBuyListAdapter.ViewHolder> {

    private Context context;
    private List<ItemWantBuy> wantBuyItems;
    private GameItemDAO gameItemDAO;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ItemWantBuy item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public WantBuyListAdapter(Context context, List<ItemWantBuy> wantBuyItems) {
        this.context = context;
        this.wantBuyItems = wantBuyItems;
        this.gameItemDAO = new GameItemDAO(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_want_buy_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemWantBuy item = wantBuyItems.get(position);

        // 获取饰品名称
        gameItemDAO.open();
        GameItem gameItem = gameItemDAO.getItemById(item.getItem_id());
        gameItemDAO.close();

        if (gameItem != null) {
            holder.tvItemName.setText(gameItem.getItem_name());
        } else {
            holder.tvItemName.setText("未知饰品");
        }

        holder.tvPrice.setText("求购价格: ¥" + String.format("%.2f", item.getWant_price()));
        holder.tvQuantity.setText("数量: " + item.getQuantity() + " 件");
        holder.tvBuyerId.setText("买家ID: " + item.getUser_id());

        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wantBuyItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvPrice, tvQuantity, tvBuyerId;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvBuyerId = itemView.findViewById(R.id.tvBuyerId);
        }
    }
}