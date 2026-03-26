package com.example.miniprojecttest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniprojecttest.adapters.CheckoutAdapter;
import com.example.miniprojecttest.dal.AppDatabase;
import com.example.miniprojecttest.dal.OrderDAO;
import com.example.miniprojecttest.dal.OrderDetailDAO;
import com.example.miniprojecttest.dal.ProductDAO;
import com.example.miniprojecttest.entities.Order;
import com.example.miniprojecttest.entities.OrderDetail;
import com.example.miniprojecttest.entities.Product;
import com.example.miniprojecttest.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private RecyclerView rvCheckoutItems;
    private TextView tvCheckoutSubtotal;
    private TextView tvCheckoutTax;
    private TextView tvCheckoutTotal;
    private Button btnSubmitOrder;

    private AppDatabase db;
    private CheckoutAdapter checkoutAdapter;
    private SessionManager sessionManager;
    private int currentOrderId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        rvCheckoutItems = findViewById(R.id.rvCheckoutItems);
        tvCheckoutSubtotal = findViewById(R.id.tvCheckoutSubtotal);
        tvCheckoutTax = findViewById(R.id.tvCheckoutTax);
        tvCheckoutTotal = findViewById(R.id.tvCheckoutTotal);
        btnSubmitOrder = findViewById(R.id.btnSubmitOrder);

        db = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarCheckout);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        resolveAndLoadOrder();

        btnSubmitOrder.setOnClickListener(v -> {
            submitOrder();
        });
    }

    private void resolveAndLoadOrder() {
        int orderIdFromIntent = getIntent().getIntExtra("orderId", -1);

        if (orderIdFromIntent != -1) {
            currentOrderId = orderIdFromIntent;
            loadOrderData(currentOrderId);
            return;
        }

        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        int userId = sessionManager.getUserId();
        Order pendingOrder = db.orderDAO().findPendingByUserId(userId);
        if (pendingOrder == null) {
            Toast.makeText(this, "No pending order", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentOrderId = pendingOrder.getId();
        loadOrderData(currentOrderId);
    }

    private void loadOrderData(int orderId) {
        OrderDAO orderDAO = db.orderDAO();
        OrderDetailDAO orderDetailDAO = db.orderDetailDAO();
        ProductDAO productDAO = db.productDAO();

        Order order = orderDAO.findById(orderId);
        if (order != null) {
            List<OrderDetail> details = orderDetailDAO.findByOrderId(orderId);
            
            List<CheckoutAdapter.CheckoutItem> items = new ArrayList<>();
            double subtotal = 0;
            
            for (OrderDetail detail : details) {
                Product p = productDAO.findById(detail.getProductId());
                items.add(new CheckoutAdapter.CheckoutItem(detail, p));
                subtotal += (detail.getQuantity() * detail.getUnitPrice());
            }

            rvCheckoutItems.setLayoutManager(new LinearLayoutManager(this));
            checkoutAdapter = new CheckoutAdapter(this, items);
            rvCheckoutItems.setAdapter(checkoutAdapter);
            
            double tax = subtotal * 0.05;
            double total = subtotal + tax;

            tvCheckoutSubtotal.setText(String.format("$%.2f", subtotal));
            tvCheckoutTax.setText(String.format("$%.2f", tax));
            tvCheckoutTotal.setText(String.format("$%.2f", total));

        }
    }

    private void submitOrder() {
        if (currentOrderId == -1) {
            Toast.makeText(this, "No order selected", Toast.LENGTH_SHORT).show();
            return;
        }

        OrderDAO orderDAO = db.orderDAO();
        orderDAO.updateStatus(currentOrderId, "Paid");

        Intent invoiceIntent = new Intent(this, InvoiceActivity.class);
        invoiceIntent.putExtra("orderId", currentOrderId);
        startActivity(invoiceIntent);
        finish();
    }
}
