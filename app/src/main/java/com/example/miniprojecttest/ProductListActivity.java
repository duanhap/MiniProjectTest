package com.example.miniprojecttest;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniprojecttest.adapters.ProductAdapter;
import com.example.miniprojecttest.activities.LoginActivity;
import com.example.miniprojecttest.dal.AppDatabase;
import com.example.miniprojecttest.dal.ProductDAO;
import com.example.miniprojecttest.entities.Order;
import com.example.miniprojecttest.entities.Product;
import com.example.miniprojecttest.session.SessionManager;

import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private AppDatabase db;
    private ProductDAO productDAO;
    private SessionManager sessionManager;
    private int categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        rvProducts = findViewById(R.id.rvProducts);

        // Initialize DB and DAO
        db = AppDatabase.getInstance(this);
        productDAO = db.productDAO();
        sessionManager = new SessionManager(this);
        categoryId = getIntent().getIntExtra("categoryId", -1);

        List<Product> products = loadProducts("");

        // Setup RecyclerView
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(this, products);
        rvProducts.setAdapter(productAdapter);

        // Set toolbar back button
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product_list, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchView != null) {
            searchView.setQueryHint("Search product...");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    filterProducts(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filterProducts(newText);
                    return true;
                }
            });
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
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

    private List<Product> loadProducts(String keyword) {
        String safeKeyword = keyword == null ? "" : keyword.trim();
        if (categoryId != -1) {
            if (safeKeyword.isEmpty()) {
                return productDAO.findByCategoryId(categoryId);
            }
            return productDAO.searchByCategoryAndName(categoryId, safeKeyword);
        }

        if (safeKeyword.isEmpty()) {
            return productDAO.getAll();
        }
        return productDAO.searchByName(safeKeyword);
    }

    private void filterProducts(String keyword) {
        List<Product> products = loadProducts(keyword);
        productAdapter.updateProducts(products);
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
