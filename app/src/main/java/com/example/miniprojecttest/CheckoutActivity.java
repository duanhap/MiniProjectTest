package com.example.miniprojecttest;

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

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarCheckout);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        int orderId = getIntent().getIntExtra("orderId", -1);
        if (orderId != -1) {
            loadOrderData(orderId);
        } else {
            Toast.makeText(this, "No Order ID provided", Toast.LENGTH_SHORT).show();
        }

        btnSubmitOrder.setOnClickListener(v -> {
            Toast.makeText(this, "Order Submitted!", Toast.LENGTH_SHORT).show();
            finish();
        });
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
            
            double tax = subtotal * 0.05; // 5% tax from XML example
            double total = subtotal + tax;

            tvCheckoutSubtotal.setText(String.format("$%.2f", subtotal));
            tvCheckoutTax.setText(String.format("$%.2f", tax));
            tvCheckoutTotal.setText(String.format("$%.2f", total));
            
            // Optionally update the total price in DB
            if (order.getTotalPrice() != total) {
                // Wait, Order doesn't have an update method in DAO?
                // Not required.
            }

        }
    }
}
