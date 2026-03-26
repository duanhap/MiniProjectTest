package com.example.miniprojecttest.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniprojecttest.dal.DatabaseSeeder;
import com.example.miniprojecttest.R;
import com.example.miniprojecttest.entities.Product;
import com.example.miniprojecttest.ProductDetailActivity;
import com.example.miniprojecttest.activities.LoginActivity;
import com.example.miniprojecttest.session.SessionManager;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvProductName.setText(product.getName());
        holder.tvProductPrice.setText(String.format("$%.2f", product.getPrice()));
        holder.ivProductImage.setImageResource(
            DatabaseSeeder.getDrawableId(context, product.getImage())
        );

        holder.itemView.setOnClickListener(v -> {
            SessionManager sessionManager = new SessionManager(context);
            Intent intent;

            if (!sessionManager.isLoggedIn()) {
                intent = new Intent(context, LoginActivity.class);
                intent.putExtra(LoginActivity.EXTRA_PRODUCT_ID, product.getId());
            } else {
                intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("productId", product.getId());
            }

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }

    public void updateProducts(List<Product> products) {
        this.productList = products;
        notifyDataSetChanged();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName;
        TextView tvProductPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
        }
    }
}
