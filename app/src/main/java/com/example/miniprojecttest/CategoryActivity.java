package com.example.miniprojecttest;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniprojecttest.adapters.CategoryAdapter;
import com.example.miniprojecttest.dal.AppDatabase;
import com.example.miniprojecttest.dal.CategoryDAO;
import com.example.miniprojecttest.entities.Category;

import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView rvCategories;
    private CategoryAdapter categoryAdapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        rvCategories = findViewById(R.id.rvCategories);
        
        db = AppDatabase.getInstance(this);
        CategoryDAO categoryDAO = db.categoryDAO();
        
        List<Category> categories = categoryDAO.getAll();

        rvCategories.setLayoutManager(new GridLayoutManager(this, 3));
        categoryAdapter = new CategoryAdapter(this, categories);
        rvCategories.setAdapter(categoryAdapter);
        
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarCategory);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }
}
