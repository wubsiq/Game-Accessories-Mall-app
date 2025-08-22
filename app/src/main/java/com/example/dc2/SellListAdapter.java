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
import com.example.dc2.table.ItemForSale;
import java.util.List;

public class SellListAdapter extends RecyclerView.Adapter<SellListAdapter.ViewHolder> {

    private Context context;
    private List<ItemForSale> sellList;
    private GameItemDAO gameItemDAO;

    public SellListAdapter(Context context, List<ItemForSale> sellList) {
        this.context = context;
        this.sellList = sellList;
        this.gameItemDAO = new GameItemDAO(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sell_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemForSale item = sellList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return sellList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvPrice, tvStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        void bind(ItemForSale item) {
            // 获取饰品名称（异步）
            gameItemDAO.open();
            GameItem gameItem = gameItemDAO.getItemById(item.getItem_id());
            gameItemDAO.close();

            if (gameItem != null) {
                tvItemName.setText(gameItem.getItem_name());
            } else {
                tvItemName.setText("未知饰品");
            }

            // 设置出售价格
            tvPrice.setText("出售价格: ¥" + String.format("%.2f", item.getPrice()));

            // 设置状态（示例：所有在售物品状态为"在售中"）
            tvStatus.setText("状态: 在售中");
        }
    }
}