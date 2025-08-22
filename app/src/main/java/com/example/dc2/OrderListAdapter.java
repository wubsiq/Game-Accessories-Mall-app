package com.example.dc2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dc2.tabledao.GameItemDAO;
import com.example.dc2.table.BuyRecord;
import com.example.dc2.table.GameItem;

import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {

    private Context context;
    private List<BuyRecord> orderList;
    private GameItemDAO gameItemDAO;

    public OrderListAdapter(Context context, List<BuyRecord> orderList) {
        this.context = context;
        this.orderList = orderList;
        this.gameItemDAO = new GameItemDAO(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BuyRecord record = orderList.get(position);

        // 显示订单ID
        holder.tvOrderId.setText("订单ID: " + record.getRecord_id());

        // 显示商品ID和名称
        if (record.getGameItem() != null) {
            holder.tvItemId.setText("饰品ID: " + record.getItem_id() + " (" + record.getGameItem().getItem_name() + ")");
        } else {
            holder.tvItemId.setText("饰品ID: " + record.getItem_id() + " (未知商品)");
        }

        // 显示购买价格
        holder.tvPrice.setText("购买价格: ¥" + String.format("%.2f", record.getPurchase_price()));

        // 显示购买时间
        holder.tvTime.setText("购买时间: " + record.getPurchase_time());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvItemId, tvPrice, tvTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvItemId = itemView.findViewById(R.id.tvItemId);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}