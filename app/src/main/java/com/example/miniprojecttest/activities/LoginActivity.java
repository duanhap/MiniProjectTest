package com.example.miniprojecttest.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.miniprojecttest.CategoryActivity;
import com.example.miniprojecttest.ProductDetailActivity;
import com.example.miniprojecttest.R;
import com.example.miniprojecttest.dal.AppDatabase;
import com.example.miniprojecttest.dal.DatabaseSeeder;
import com.example.miniprojecttest.entities.User;
import com.example.miniprojecttest.session.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "productId";

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private AppDatabase db;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Launcher screen should always ensure sample data exists.
        DatabaseSeeder.seed(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        db = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            navigateAfterLogin(sessionManager.getUserId(), sessionManager.getUsername());
            return;
        }

        Button btnLogin = findViewById(R.id.btnLogin);
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> login());
        }
    }

    private void login() {
        String username = etUsername.getText() == null ? "" : etUsername.getText().toString().trim();
        String password = etPassword.getText() == null ? "" : etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = db.userDAO().login(username, password);
        if (user == null) {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            return;
        }

        sessionManager.saveLogin(user.getId(), user.getUsername());
        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
        navigateAfterLogin(user.getId(), user.getUsername());
    }

    private void navigateAfterLogin(int userId, String username) {
        int productId = getIntent().getIntExtra(EXTRA_PRODUCT_ID, -1);
        Intent intent;

        if (productId != -1) {
            intent = new Intent(this, ProductDetailActivity.class);
            intent.putExtra(EXTRA_PRODUCT_ID, productId);
        } else {
            intent = new Intent(this, CategoryActivity.class);
        }

        intent.putExtra("userId", userId);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }
}
