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

public class BuyListAdapter extends RecyclerView.Adapter<BuyListAdapter.ViewHolder> {

    private Context context;
    private List<ItemWantBuy> wantBuyList;
    private GameItemDAO gameItemDAO;

    public BuyListAdapter(Context context, List<ItemWantBuy> wantBuyList, GameItemDAO gameItemDAO) {
        this.context = context;
        this.wantBuyList = wantBuyList;
        this.gameItemDAO = gameItemDAO;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_buy_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemWantBuy item = wantBuyList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return wantBuyList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvWantPrice, tvQuantity;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvWantPrice = itemView.findViewById(R.id.tvWantPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);

        }

        void bind(ItemWantBuy item) {
            // 获取饰品名称
            gameItemDAO.open();
            GameItem gameItem = gameItemDAO.getItemById(item.getItem_id());
            gameItemDAO.close();

            if (gameItem != null) {
                tvItemName.setText(gameItem.getItem_name());
            } else {
                tvItemName.setText("未知饰品");
            }

            // 设置求购价格和数量
            tvWantPrice.setText("求购价格: ¥" + String.format("%.2f", item.getWant_price()));
            tvQuantity.setText("数量: " + item.getQuantity() + " 件");
        }
    }
}