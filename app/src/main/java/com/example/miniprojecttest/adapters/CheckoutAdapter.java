package com.example.miniprojecttest.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniprojecttest.R;
import com.example.miniprojecttest.dal.DatabaseSeeder;
import com.example.miniprojecttest.entities.OrderDetail;
import com.example.miniprojecttest.entities.Product;

import java.util.List;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder> {

    public static class CheckoutItem {
        public OrderDetail orderDetail;
        public Product product;

        public CheckoutItem(OrderDetail orderDetail, Product product) {
            this.orderDetail = orderDetail;
            this.product = product;
        }
    }

    private Context context;
    private List<CheckoutItem> checkoutItems;

    public CheckoutAdapter(Context context, List<CheckoutItem> checkoutItems) {
        this.context = context;
        this.checkoutItems = checkoutItems;
    }

    @NonNull
    @Override
    public CheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_checkout, parent, false);
        return new CheckoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutViewHolder holder, int position) {
        CheckoutItem item = checkoutItems.get(position);
        
        if (item.product != null) {
            holder.tvCheckoutName.setText(item.product.getName());
            holder.ivCheckoutThumb.setImageResource(
                    DatabaseSeeder.getDrawableId(context, item.product.getImage())
            );
        } else {
            holder.tvCheckoutName.setText("Product " + item.orderDetail.getProductId());
            holder.ivCheckoutThumb.setImageResource(android.R.drawable.ic_menu_gallery);
        }
        
        holder.tvCheckoutQuantity.setText("Qty: " + item.orderDetail.getQuantity());
        holder.tvCheckoutPrice.setText(String.format("$%.2f", item.orderDetail.getUnitPrice()));
    }

    @Override
    public int getItemCount() {
        return checkoutItems == null ? 0 : checkoutItems.size();
    }

    public static class CheckoutViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCheckoutThumb;
        TextView tvCheckoutName;
        TextView tvCheckoutQuantity;
        TextView tvCheckoutPrice;

        public CheckoutViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCheckoutThumb = itemView.findViewById(R.id.ivCheckoutThumb);
            tvCheckoutName = itemView.findViewById(R.id.tvCheckoutName);
            tvCheckoutQuantity = itemView.findViewById(R.id.tvCheckoutQuantity);
            tvCheckoutPrice = itemView.findViewById(R.id.tvCheckoutPrice);
        }
    }
}
