package com.example.miniprojecttest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.miniprojecttest.dal.AppDatabase;
import com.example.miniprojecttest.dal.OrderDAO;
import com.example.miniprojecttest.dal.OrderDetailDAO;
import com.example.miniprojecttest.dal.ProductDAO;
import com.example.miniprojecttest.entities.Order;
import com.example.miniprojecttest.entities.OrderDetail;
import com.example.miniprojecttest.entities.Product;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView ivProductDetailImage;
    private TextView tvDetailTitle;
    private TextView tvDetailPrice;
    private TextView tvDetailDescription;
    private Button btnAddToCart;

    private AppDatabase db;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ivProductDetailImage = findViewById(R.id.ivProductDetailImage);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        db = AppDatabase.getInstance(this);
        ProductDAO productDAO = db.productDAO();

        int productId = getIntent().getIntExtra("productId", -1);
        if (productId != -1) {
            product = productDAO.findById(productId);
            if (product != null) {
                tvDetailTitle.setText(product.getName());
                tvDetailPrice.setText(String.format("$%.2f", product.getPrice()));
                tvDetailDescription.setText("Description for " + product.getName() + ".\nHigh quality materials.");
            }
        }

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarDetail);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        btnAddToCart.setOnClickListener(v -> {
            if (product != null) {
                createOrderAndCheckout();
            }
        });
    }

    private void createOrderAndCheckout() {
        OrderDAO orderDAO = db.orderDAO();
        OrderDetailDAO orderDetailDAO = db.orderDetailDAO();

        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        
        // Setup a mock user id for the order, assuming user ID 1 exists
        Order newOrder = new Order(1, currentDate, product.getPrice());
        long orderId = orderDAO.insert(newOrder);

        OrderDetail detail = new OrderDetail((int) orderId, product.getId(), 1, product.getPrice());
        orderDetailDAO.insert(detail);

        Toast.makeText(this, "Added to Cart!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(ProductDetailActivity.this, CheckoutActivity.class);
        intent.putExtra("orderId", (int) orderId);
        startActivity(intent);
    }
}
