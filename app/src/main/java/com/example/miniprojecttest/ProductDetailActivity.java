package com.example.miniprojecttest;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.miniprojecttest.dal.AppDatabase;
import com.example.miniprojecttest.dal.DatabaseSeeder;
import com.example.miniprojecttest.dal.OrderDAO;
import com.example.miniprojecttest.dal.OrderDetailDAO;
import com.example.miniprojecttest.dal.ProductDAO;
import com.example.miniprojecttest.entities.Order;
import com.example.miniprojecttest.entities.OrderDetail;
import com.example.miniprojecttest.entities.Product;
import com.example.miniprojecttest.activities.LoginActivity;
import com.example.miniprojecttest.session.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView ivProductDetailImage;
    private TextView tvDetailTitle;
    private TextView tvDetailPrice;
    private TextView tvDetailDescription;
    private Button btnAddToCart;

    private AppDatabase db;
    private Product product;
    private SessionManager sessionManager;

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
        sessionManager = new SessionManager(this);
        ProductDAO productDAO = db.productDAO();

        int productId = getIntent().getIntExtra("productId", -1);
        if (productId != -1) {
            product = productDAO.findById(productId);
            if (product != null) {
                tvDetailTitle.setText(product.getName());
                tvDetailPrice.setText(String.format("$%.2f", product.getPrice()));
                tvDetailDescription.setText("Description for " + product.getName() + ".\nHigh quality materials.");
                ivProductDetailImage.setImageResource(
                        DatabaseSeeder.getDrawableId(this, product.getImage())
                );
            }
        }

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarDetail);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // Animate content on enter
        if (tvDetailTitle != null) {
            Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up_fade_in);
            slideUp.setStartOffset(100);
            tvDetailTitle.startAnimation(slideUp);
        }
        if (tvDetailPrice != null) {
            Animation slideUp2 = AnimationUtils.loadAnimation(this, R.anim.slide_up_fade_in);
            slideUp2.setStartOffset(180);
            tvDetailPrice.startAnimation(slideUp2);
        }
        if (btnAddToCart != null) {
            Animation popIn = AnimationUtils.loadAnimation(this, R.anim.scale_pop_in);
            popIn.setStartOffset(350);
            btnAddToCart.startAnimation(popIn);
        }

        btnAddToCart.setOnClickListener(v -> {
            if (product != null) {
                // Bounce press effect
                v.animate().scaleX(0.92f).scaleY(0.92f).setDuration(100).withEndAction(() ->
                    v.animate().scaleX(1f).scaleY(1f).setDuration(150).withEndAction(
                        this::addToPendingOrderAndCheckout
                    ).start()
                ).start();
            }
        });
    }


    private void addToPendingOrderAndCheckout() {
        if (!sessionManager.isLoggedIn()) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.putExtra(LoginActivity.EXTRA_PRODUCT_ID, product.getId());
            startActivity(loginIntent);
            finish();
            return;
        }

        int userId = sessionManager.getUserId();
        if (userId <= 0) {
            Toast.makeText(this, "Invalid user session. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        OrderDAO orderDAO = db.orderDAO();
        OrderDetailDAO orderDetailDAO = db.orderDetailDAO();

        Order pendingOrder = orderDAO.findPendingByUserId(userId);
        int orderId;
        if (pendingOrder == null) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            Order newOrder = new Order(userId, currentDate, 0, "Pending");
            orderId = (int) orderDAO.insert(newOrder);
        } else {
            orderId = pendingOrder.getId();
        }

        OrderDetail existingDetail = orderDetailDAO.findByOrderIdAndProductId(orderId, product.getId());
        if (existingDetail == null) {
            OrderDetail detail = new OrderDetail(orderId, product.getId(), 1, product.getPrice());
            orderDetailDAO.insert(detail);
        } else {
            int newQuantity = existingDetail.getQuantity() + 1;
            orderDetailDAO.updateQuantityAndPrice(existingDetail.getId(), newQuantity, product.getPrice());
        }

        updateOrderTotal(orderId, orderDAO, orderDetailDAO);

        Toast.makeText(this, "Added to pending order", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(ProductDetailActivity.this, CheckoutActivity.class);
        intent.putExtra("orderId", orderId);
        startActivity(intent);
    }

    private void updateOrderTotal(int orderId, OrderDAO orderDAO, OrderDetailDAO orderDetailDAO) {
        List<OrderDetail> details = orderDetailDAO.findByOrderId(orderId);
        double total = 0;
        for (OrderDetail detail : details) {
            total += detail.getQuantity() * detail.getUnitPrice();
        }
        orderDAO.updateTotalPrice(orderId, total);
    }
}
