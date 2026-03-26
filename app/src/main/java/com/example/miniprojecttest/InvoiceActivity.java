package com.example.miniprojecttest;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.miniprojecttest.dal.AppDatabase;
import com.example.miniprojecttest.entities.Order;

public class InvoiceActivity extends AppCompatActivity {

    private TextView tvInvoiceOrderId;
    private TextView tvInvoiceDate;
    private TextView tvInvoiceStatus;
    private TextView tvInvoiceTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        tvInvoiceOrderId = findViewById(R.id.tvInvoiceOrderId);
        tvInvoiceDate = findViewById(R.id.tvInvoiceDate);
        tvInvoiceStatus = findViewById(R.id.tvInvoiceStatus);
        tvInvoiceTotal = findViewById(R.id.tvInvoiceTotal);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarInvoice);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        int orderId = getIntent().getIntExtra("orderId", -1);
        if (orderId == -1) {
            Toast.makeText(this, "No invoice found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        AppDatabase db = AppDatabase.getInstance(this);
        Order order = db.orderDAO().findById(orderId);
        if (order == null) {
            Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvInvoiceOrderId.setText("#" + order.getId());
        tvInvoiceDate.setText(order.getOrderDate());
        tvInvoiceStatus.setText(order.getStatus());
        tvInvoiceTotal.setText(String.format("$%.2f", order.getTotalPrice()));
    }
}
