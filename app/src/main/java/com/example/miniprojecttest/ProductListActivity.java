package com.example.miniprojecttest;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniprojecttest.adapters.ProductAdapter;
import com.example.miniprojecttest.dal.AppDatabase;
import com.example.miniprojecttest.dal.ProductDAO;
import com.example.miniprojecttest.entities.Product;

import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        rvProducts = findViewById(R.id.rvProducts);
        
        // Initialize DB and DAO
        db = AppDatabase.getInstance(this);
        ProductDAO productDAO = db.productDAO();
        
        // Fetch data based on intent
        int categoryId = getIntent().getIntExtra("categoryId", -1);
        List<Product> products;
        
        if (categoryId != -1) {
            products = productDAO.findByCategoryId(categoryId);
        } else {
            products = productDAO.getAll();
        }

        // Setup RecyclerView
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(this, products);
        rvProducts.setAdapter(productAdapter);
        
        // Set toolbar back button
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }
}
