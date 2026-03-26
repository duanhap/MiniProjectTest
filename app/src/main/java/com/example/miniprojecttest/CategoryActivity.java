package com.example.miniprojecttest;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniprojecttest.adapters.CategoryAdapter;
import com.example.miniprojecttest.activities.LoginActivity;
import com.example.miniprojecttest.dal.AppDatabase;
import com.example.miniprojecttest.dal.CategoryDAO;
import com.example.miniprojecttest.entities.Order;
import com.example.miniprojecttest.entities.Category;
import com.example.miniprojecttest.session.SessionManager;

import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView rvCategories;
    private CategoryAdapter categoryAdapter;
    private AppDatabase db;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        rvCategories = findViewById(R.id.rvCategories);

        db = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);
        CategoryDAO categoryDAO = db.categoryDAO();

        List<Category> categories = categoryDAO.getAll();

        rvCategories.setLayoutManager(new GridLayoutManager(this, 3));
        categoryAdapter = new CategoryAdapter(this, categories);
        rvCategories.setAdapter(categoryAdapter);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarCategory);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_all_products) {
            startActivity(new Intent(this, ProductListActivity.class));
            return true;
        }
        if (itemId == R.id.action_cart) {
            openPendingCart();
            return true;
        }
        if (itemId == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openPendingCart() {
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        int userId = sessionManager.getUserId();
        Order pendingOrder = db.orderDAO().findPendingByUserId(userId);
        if (pendingOrder == null) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent checkoutIntent = new Intent(this, CheckoutActivity.class);
        checkoutIntent.putExtra("orderId", pendingOrder.getId());
        startActivity(checkoutIntent);
    }

    private void logout() {
        sessionManager.clearSession();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
