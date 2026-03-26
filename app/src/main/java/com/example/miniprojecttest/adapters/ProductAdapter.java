package com.example.miniprojecttest.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    // Track last animated position for stagger effect
    private int lastPosition = -1;

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
        holder.tvProductPrice.setText(String.format("$%.0f", product.getPrice()));
        holder.ivProductImage.setImageResource(
                DatabaseSeeder.getDrawableId(context, product.getImage())
        );

        // Staggered item entrance animation
        if (position > lastPosition) {
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.item_animation_fall_down);
            anim.setStartOffset(position * 40L); // Stagger by 40ms per item
            holder.itemView.startAnimation(anim);
            lastPosition = position;
        }

        holder.itemView.setOnClickListener(v -> {
            // Press scale effect
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).withEndAction(() ->
                v.animate().scaleX(1f).scaleY(1f).setDuration(80).withEndAction(() -> {
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
                }).start()
            ).start();
        });
    }

    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }

    public void updateProducts(List<Product> products) {
        this.productList = products;
        lastPosition = -1;
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
