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

import com.example.miniprojecttest.R;
import com.example.miniprojecttest.entities.Category;
import com.example.miniprojecttest.ProductListActivity;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categoryList;
    private int lastPosition = -1;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.tvCategoryName.setText(category.getName());

        // Staggered entrance animation
        if (position > lastPosition) {
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.item_animation_fall_down);
            anim.setStartOffset(position * 50L);
            holder.itemView.startAnimation(anim);
            lastPosition = position;
        }

        holder.itemView.setOnClickListener(v -> {
            // Press scale feedback
            v.animate().scaleX(0.93f).scaleY(0.93f).setDuration(80).withEndAction(() ->
                v.animate().scaleX(1f).scaleY(1f).setDuration(80).withEndAction(() -> {
                    Intent intent = new Intent(context, ProductListActivity.class);
                    intent.putExtra("categoryId", category.getId());
                    context.startActivity(intent);
                }).start()
            ).start();
        });
    }

    @Override
    public int getItemCount() {
        return categoryList == null ? 0 : categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategoryIcon;
        TextView tvCategoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}
